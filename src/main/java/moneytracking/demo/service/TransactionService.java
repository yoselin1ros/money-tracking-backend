package moneytracking.demo.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import moneytracking.demo.dto.AccountResponseDTO;
import moneytracking.demo.dto.CustomUserDetails;
import moneytracking.demo.dto.TransactionRequestDTO;
import moneytracking.demo.dto.TransactionResponseDTO;
import moneytracking.demo.entity.AccountEntity;
import moneytracking.demo.entity.CategoryEntity;
import moneytracking.demo.entity.RefItemEntity;
import moneytracking.demo.entity.TransactionEntity;
import moneytracking.demo.entity.UserEntity;
import moneytracking.demo.exception.ResourceNotFoundException;
import moneytracking.demo.repository.AccountRepository;
import moneytracking.demo.repository.CategoryRepository;
import moneytracking.demo.repository.RefItemRepository;
import moneytracking.demo.repository.TransactionRepository;
import moneytracking.demo.repository.UserRepository;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final BudgetService budgetService;
    private final HistoryService historyService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String OBJECT_TYPE_TRANSACTION = "transaction";
    private static final String OBJECT_TYPE_ACCOUNT = "account";

    public TransactionService(
        TransactionRepository transactionRepository, UserRepository userRepository, AccountRepository accountRepository, 
        CategoryRepository categoryRepository, RefItemRepository refItemRepository, BudgetService budgetService, 
        HistoryService historyService
    ) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
        this.budgetService = budgetService;
        this.historyService = historyService;
    }

    @Transactional(readOnly = true)
    public List<TransactionResponseDTO> listTransactions(Long userId, Long categoryId, String period, String startDate, String endDate) {
        List<TransactionEntity> transactions = transactionRepository.findByUserIdOrderByIdAsc(userId);
        if (categoryId != null) {
            transactions = transactions.stream()
                .filter(t -> t.getCategory().getId().equals(categoryId))
                .toList();
        }

        if (period != null) {
            switch (period) {
                case "day":
                    LocalDate today = LocalDate.now();
                    Instant startOfDay = today.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
                    Instant endOfDay = today.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant();
                    transactions = transactions.stream()
                        .filter(t -> t.getCreatedAt().isAfter(startOfDay) && t.getCreatedAt().isBefore(endOfDay))
                        .toList();
                    break;
                
                case "week":
                    LocalDate startOfWeek = LocalDate.now().with(java.time.DayOfWeek.MONDAY);
                    Instant startOfWeekInstant = startOfWeek.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
                    LocalDate endOfWeek = startOfWeek.plusDays(6);
                    Instant endOfWeekInstant = endOfWeek.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant();
                    transactions = transactions.stream()
                        .filter(t -> t.getCreatedAt().isAfter(startOfWeekInstant) && t.getCreatedAt().isBefore(endOfWeekInstant))
                        .toList();
                    break;
                
                case "month":
                    LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
                    Instant startOfMonthInstant = startOfMonth.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
                    LocalDate endOfMonth = startOfMonth.plusMonths(1).minusDays(1);
                    Instant endOfMonthInstant = endOfMonth.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant();
                    transactions = transactions.stream()
                        .filter(t -> t.getCreatedAt().isAfter(startOfMonthInstant) && t.getCreatedAt().isBefore(endOfMonthInstant))
                        .toList();
                    break;

                case "year":
                    LocalDate startOfYear = LocalDate.now().withDayOfYear(1);
                    Instant startOfYearInstant = startOfYear.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
                    LocalDate endOfYear = startOfYear.plusMonths(12).minusDays(1);
                    Instant endOfYearInstant = endOfYear.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant();
                    transactions = transactions.stream()
                        .filter(t -> t.getCreatedAt().isAfter(startOfYearInstant) && t.getCreatedAt().isBefore(endOfYearInstant))
                        .toList();
                    break;

                case "custom":
                    if (startDate != null && endDate != null) {
                        Instant startInstant = LocalDate.parse(startDate).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
                        Instant endInstant = LocalDate.parse(endDate).atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant();
                        if (startInstant.isAfter(endInstant)) {
                            throw new IllegalArgumentException("Start date cannot be after end date");
                        }
                        transactions = transactions.stream()
                            .filter(t -> t.getCreatedAt().isAfter(startInstant) && t.getCreatedAt().isBefore(endInstant))
                            .toList();
                    } else {
                        throw new IllegalArgumentException("Both startDate and endDate must be provided for custom period");
                    }
                    break;

                default:
                    break;
            }
        }

        return transactions.stream()
            .map(this::mapToResponseDTO)
            .toList();
    }

    @Transactional
    public TransactionResponseDTO createTransaction(TransactionRequestDTO request) {
        UserEntity user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        AccountEntity account = accountRepository.findById(request.getAccountId())
            .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        
        CategoryEntity category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        
        RefItemEntity type = category.getType(); // Assuming the type is derived from the category

        AccountResponseDTO previousAccount = account.mapToResponseDTO(account);

        // Checking/updating amount in account for expense transactions
        if (account.getCurrentBalance().compareTo(request.getAmount()) < 0 && type.getName().equals("expense")) {
            throw new IllegalArgumentException("Insufficient funds in the account for this transaction");
        }
        if (type.getName().equals("expense")) {
            account.setCurrentBalance(account.getCurrentBalance().subtract(request.getAmount()));
        } else if (type.getName().equals("income")) {
            account.setCurrentBalance(account.getCurrentBalance().add(request.getAmount()));
        }
        accountRepository.save(account);

        AccountResponseDTO updatedAccount = account.mapToResponseDTO(account);

        try {
            String newValueAccount = objectMapper.writeValueAsString(updatedAccount);
            String previousValueAccount = objectMapper.writeValueAsString(previousAccount);
            historyService.appendEntry(user.getId(), OBJECT_TYPE_ACCOUNT, updatedAccount.getId(), "UPDATE", previousValueAccount, newValueAccount);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert entity to JSON string", e);
        }
        
        TransactionEntity transaction = new TransactionEntity();
        transaction.setUser(user);
        transaction.setAccount(account);
        transaction.setCategory(category);
        transaction.setType(type);
        transaction.setAmount(request.getAmount());
        transaction.setNote(request.getNote());

        TransactionEntity savedTransaction = transactionRepository.save(transaction);

        // evaluating budgets after creating a transaction
        budgetService.evaluateBudgets(user.getId(), category.getId());

        TransactionResponseDTO responseDTO = mapToResponseDTO(savedTransaction);

        try { 
            String newValue = objectMapper.writeValueAsString(responseDTO);
            historyService.appendEntry(user.getId(), OBJECT_TYPE_TRANSACTION, savedTransaction.getId(), "CREATE", null, newValue);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert entity to JSON string", e);
        }

        return responseDTO;
    }

    @Transactional
    public TransactionResponseDTO updateTransaction(Long transactionId, TransactionRequestDTO request) {
        TransactionEntity transaction = transactionRepository.findById(transactionId)
            .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        if (currentUser != null && currentUser.isAuthenticated() 
            && currentUser.getPrincipal() instanceof CustomUserDetails userDetails) {
            if (!transaction.getUser().getId().equals(userDetails.getId())) {
                throw new SecurityException("You are not authorized to update this transaction");
            }
        } else {
            throw new SecurityException("Authentication information is missing or invalid");
        }

        AccountEntity account = accountRepository.findById(request.getAccountId())
            .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        CategoryEntity category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        RefItemEntity type = category.getType(); // Assuming the type is derived from the category

        AccountResponseDTO previousAccount = account.mapToResponseDTO(account);

        // Checking/updating amount in account for expense transactions
        if (account.getCurrentBalance().compareTo(request.getAmount()) < 0 && type.getName().equals("expense")) {
            throw new IllegalArgumentException("Insufficient funds in the account for this transaction");
        }
        if (type.getName().equals("expense")) {
            account.setCurrentBalance(account.getCurrentBalance().add(transaction.getAmount())); // Revert previous amount
            account.setCurrentBalance(account.getCurrentBalance().subtract(request.getAmount()));
        } else if (type.getName().equals("income")) {
            account.setCurrentBalance(account.getCurrentBalance().subtract(transaction.getAmount())); // Revert previous amount
            account.setCurrentBalance(account.getCurrentBalance().add(request.getAmount()));
        }
        accountRepository.save(account);

        AccountResponseDTO updatedAccount = account.mapToResponseDTO(account);

        try {
            String newValueAccount = objectMapper.writeValueAsString(updatedAccount);
            String previousValueAccount = objectMapper.writeValueAsString(previousAccount);
            historyService.appendEntry(userDetails.getId(), OBJECT_TYPE_ACCOUNT, updatedAccount.getId(), "UPDATE", previousValueAccount, newValueAccount);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert entity to JSON string", e);
        }

        TransactionResponseDTO previousTransaction = mapToResponseDTO(transaction);

        transaction.setAccount(account);
        transaction.setCategory(category);
        transaction.setType(type);
        transaction.setAmount(request.getAmount());
        transaction.setNote(request.getNote());

        TransactionEntity savedTransaction = transactionRepository.save(transaction);

        // evaluating budgets after updating a transaction
        budgetService.evaluateBudgets(userDetails.getId(), category.getId());

        TransactionResponseDTO responseDTO = mapToResponseDTO(savedTransaction);

        try {
            String newValue = objectMapper.writeValueAsString(responseDTO);
            String previousValue = objectMapper.writeValueAsString(previousTransaction);
            historyService.appendEntry(userDetails.getId(), OBJECT_TYPE_TRANSACTION, savedTransaction.getId(), "UPDATE", previousValue, newValue);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert entity to JSON string", e);
        }

        return responseDTO;
    }

    @Transactional
    public Boolean deleteTransaction(Long transactionId) {
        TransactionEntity transaction = transactionRepository.findById(transactionId)
            .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
        
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        if (currentUser != null && currentUser.isAuthenticated() 
            && currentUser.getPrincipal() instanceof CustomUserDetails userDetails) {
            if (!transaction.getUser().getId().equals(userDetails.getId())) {
                throw new SecurityException("You are not authorized to delete this transaction");
            }
        } else {
            throw new SecurityException("Authentication information is missing or invalid");
        }

        TransactionResponseDTO previousTransaction = mapToResponseDTO(transaction);
        RefItemEntity type = transaction.getType();
        AccountEntity account = transaction.getAccount();

        AccountResponseDTO previousAccount = account.mapToResponseDTO(account);

        if (type.getName().equals("expense")) {
            account.setCurrentBalance(account.getCurrentBalance().add(transaction.getAmount())); // Revert previous amount
        } else if (type.getName().equals("income")) {
            account.setCurrentBalance(account.getCurrentBalance().subtract(transaction.getAmount())); // Revert previous amount
        }
        accountRepository.save(account);
        
        AccountResponseDTO updatedAccount = account.mapToResponseDTO(account);

        try {
            String newValueAccount = objectMapper.writeValueAsString(updatedAccount);
            String previousValueAccount = objectMapper.writeValueAsString(previousAccount);
            historyService.appendEntry(userDetails.getId(), OBJECT_TYPE_ACCOUNT, updatedAccount.getId(), "UPDATE", previousValueAccount, newValueAccount);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert entity to JSON string", e);
        }

        transactionRepository.delete(transaction);

        // evaluating budgets after deleting a transaction
        budgetService.evaluateBudgets(userDetails.getId(), transaction.getCategory().getId());

        try { 
            String previousValue = objectMapper.writeValueAsString(previousTransaction);
            historyService.appendEntry(userDetails.getId(), "transaction", transactionId, "DELETE", previousValue, null);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert entity to JSON string", e);
        }
        return true;
    }

    private TransactionResponseDTO mapToResponseDTO(TransactionEntity transaction) {
        TransactionResponseDTO dto = new TransactionResponseDTO();
        dto.setId(transaction.getId());
        if (transaction.getAccount() != null) {
            dto.setAccountId(transaction.getAccount().getId());
            dto.setAccountName(transaction.getAccount().getName());
        }
        if (transaction.getCategory() != null) {
            dto.setCategoryId(transaction.getCategory().getId());
            dto.setCategoryName(transaction.getCategory().getName());
        }
        dto.setAmount(transaction.getAmount());
        if (transaction.getType() != null) {
            dto.setTypeId(transaction.getType().getId());
            dto.setTypeName(transaction.getType().getName());
        }
        dto.setNote(transaction.getNote());

        dto.setTransactionDate(transaction.getTransactionDate().toString());

        return dto;
    }
}
