package moneytracking.demo.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import moneytracking.demo.dto.BudgetRequestDTO;
import moneytracking.demo.dto.BudgetResponseDTO;
import moneytracking.demo.dto.CustomUserDetails;
import moneytracking.demo.entity.BudgetEntity;
import moneytracking.demo.entity.CategoryEntity;
import moneytracking.demo.entity.RefItemEntity;
import moneytracking.demo.entity.TransactionEntity;
import moneytracking.demo.entity.UserEntity;
import moneytracking.demo.exception.ResourceNotFoundException;
import moneytracking.demo.repository.BudgetRepository;
import moneytracking.demo.repository.CategoryRepository;
import moneytracking.demo.repository.RefItemRepository;
import moneytracking.demo.repository.TransactionRepository;
import moneytracking.demo.repository.UserRepository;

@Service
public class BudgetService {
    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RefItemRepository refItemRepository;
    private final TransactionRepository transactionRepository;
    private final HistoryService historyService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String OBJECT_TYPE_BUDGET = "budget";

    public BudgetService(
        BudgetRepository budgetRepository,
        UserRepository userRepository,
        CategoryRepository categoryRepository,
        RefItemRepository refItemRepository,
        TransactionRepository transactionRepository,
        HistoryService historyService
    ) {
        this.budgetRepository = budgetRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.refItemRepository = refItemRepository;
        this.transactionRepository = transactionRepository;
        this.historyService = historyService;
    }

    @Transactional(readOnly = true)
    public List<BudgetResponseDTO> listBudgets(Long userId) {
        List<BudgetEntity> budgets = budgetRepository.findByUserIdOrderByIdAsc(userId);
        return budgets.stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Transactional
    public BudgetResponseDTO createBudget(BudgetRequestDTO request) {
        UserEntity user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        CategoryEntity category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        
        RefItemEntity periodType = refItemRepository.findById(request.getPeriodTypeId())
            .orElseThrow(() -> new ResourceNotFoundException("Period not found"));

        // checking that the period start is before the period end
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");//TODO : move to a service
        LocalDate periodStart = LocalDate.parse(request.getPeriodStart(), formatter);
        LocalDate periodEnd = LocalDate.parse(request.getPeriodEnd(), formatter);
        if (periodStart.isAfter(periodEnd)) {
            throw new IllegalArgumentException("Period start must be before period end");
        }
        
        BudgetEntity budget = new BudgetEntity();
        budget.setUser(user);
        budget.setCategory(category);
        budget.setSpendingLimit(request.getSpendingLimit());
        budget.setPeriodStart(periodStart);
        budget.setPeriodEnd(periodEnd);
        budget.setPeriodType(periodType);

        BudgetEntity savedBudget = budgetRepository.save(budget);

        BudgetResponseDTO responseDTO = mapToResponseDTO(savedBudget);

        try { 
            String newValue = objectMapper.writeValueAsString(responseDTO);
            historyService.appendEntry(user.getId(), OBJECT_TYPE_BUDGET, savedBudget.getId(), "CREATE", null, newValue);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert entity to JSON string", e);
        }

        return responseDTO;
    }

    private BudgetResponseDTO mapToResponseDTO(BudgetEntity budget) {
        BudgetResponseDTO dto = new BudgetResponseDTO();
        dto.setId(budget.getId());
        if (budget.getCategory() != null) {
            dto.setCategoryId(budget.getCategory().getId());
            dto.setCategoryName(budget.getCategory().getName());
        }
        dto.setSpendingLimit(budget.getSpendingLimit());
        if (budget.getPeriodType() != null) {
            dto.setPeriodTypeId(budget.getPeriodType().getId());
            dto.setPeriodTypeName(budget.getPeriodType().getName());
        }
        dto.setPeriodStart(budget.getPeriodStart().toString());
        dto.setPeriodEnd(budget.getPeriodEnd().toString());
        return dto;
    }

    @Transactional
    public Boolean deleteBudget(Long budgetId) {
        BudgetEntity budget = budgetRepository.findById(budgetId)
            .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));
        
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        if (currentUser != null && currentUser.isAuthenticated() 
            && currentUser.getPrincipal() instanceof CustomUserDetails userDetails) {
            if (!budget.getUser().getId().equals(userDetails.getId())) {
                throw new SecurityException("You are not authorized to delete this budget");
            }
        } else {
            throw new SecurityException("Authentication information is missing or invalid");
        }

        BudgetResponseDTO previousBudget = mapToResponseDTO(budget);


        budgetRepository.delete(budget);

        try { 
            String previousValue = objectMapper.writeValueAsString(previousBudget);
            historyService.appendEntry(userDetails.getId(), OBJECT_TYPE_BUDGET, budget.getId(), "DELETE", previousValue, null);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert entity to JSON string", e);
        }

        return true;
    }

    @Transactional
    public void evaluateBudgets(Long userId, Long categoryId) {

        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = startOfMonth.plusMonths(1).minusDays(1);
        
        BigDecimal totalSpendingLimit = budgetRepository.findByUserIdAndCategoryIdOrderByIdAsc(userId, categoryId)
            .stream()
            .filter(b -> b.getPeriodStart().equals(startOfMonth) && b.getPeriodEnd().isEqual(endOfMonth))
            .map(BudgetEntity::getSpendingLimit)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        System.out.println("totalSpendingLimit: " + totalSpendingLimit);

        BigDecimal totalAmountTransactions = transactionRepository.findByUserIdAndCategoryIdOrderByIdAsc(userId, categoryId)
            .stream()
            .filter(t -> (t.getTransactionDate().isAfter(startOfMonth) || t.getTransactionDate().equals(startOfMonth)) && 
                (t.getTransactionDate().isBefore(endOfMonth) || t.getTransactionDate().equals(endOfMonth)))
            .map(TransactionEntity::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        System.out.println("totalAmountTransactions: " + totalAmountTransactions);

        BigDecimal threshold = totalSpendingLimit.multiply(BigDecimal.valueOf(0.8)); // Set threshold at 80% of the spending limit
        System.out.println("threshold: " + threshold);
        if (totalAmountTransactions.compareTo(totalSpendingLimit) > 0 || totalAmountTransactions.compareTo(threshold) > 0) {
            System.out.println("Budget exceeded for userId: " + userId + ", categoryId: " + categoryId);
            // TODO: Implement your logic for handling budget exceedance here
        }
    }

}
