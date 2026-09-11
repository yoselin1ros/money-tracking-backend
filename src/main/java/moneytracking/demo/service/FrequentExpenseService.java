package moneytracking.demo.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import moneytracking.demo.dto.CustomUserDetails;
import moneytracking.demo.dto.FrequentExpenseRequestDTO;
import moneytracking.demo.dto.FrequentExpenseResponseDTO;
import moneytracking.demo.dto.TemplateResponseDTO;
import moneytracking.demo.dto.TransactionResponseDTO;
import moneytracking.demo.entity.CategoryEntity;
import moneytracking.demo.entity.FrequentExpenseEntity;
import moneytracking.demo.entity.UserEntity;
import moneytracking.demo.exception.ResourceNotFoundException;
import moneytracking.demo.repository.CategoryRepository;
import moneytracking.demo.repository.FrequentExpenseRepository;
import moneytracking.demo.repository.UserRepository;

@Service
public class FrequentExpenseService {
    private final FrequentExpenseRepository frequentExpenseRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final HistoryService historyService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String OBJECT_TYPE_FREQUENT_EXPENSE = "frequent_expense";

    public FrequentExpenseService(
        FrequentExpenseRepository frequentExpenseRepository, UserRepository userRepository, 
        CategoryRepository categoryRepository, HistoryService historyService
    ) {
        this.frequentExpenseRepository = frequentExpenseRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.historyService = historyService;
    }

    @Transactional(readOnly = true)
    public List<FrequentExpenseResponseDTO> getTemplatesByUserId(Long userId) {
        List<FrequentExpenseEntity> templates = frequentExpenseRepository.findAll()
            .stream()
            .filter(template -> template.getUser().getId().equals(userId))
            .toList();

        return templates.stream()
            .map(this::mapToResponseDTO)
            .toList();
    }

    @Transactional
    public FrequentExpenseResponseDTO createTemplate(FrequentExpenseRequestDTO request) {
        UserEntity user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        CategoryEntity category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        
        FrequentExpenseEntity template = new FrequentExpenseEntity();
        template.setUser(user);
        template.setCategory(category);
        template.setName(request.getName());
        template.setAmount(request.getAmount());
        
        FrequentExpenseEntity savedTemplate = frequentExpenseRepository.save(template);

        FrequentExpenseResponseDTO responseDTO = mapToResponseDTO(savedTemplate);

        try { 
            String newValue = objectMapper.writeValueAsString(responseDTO);
            historyService.appendEntry(user.getId(), OBJECT_TYPE_FREQUENT_EXPENSE, savedTemplate.getId(), "CREATE", null, newValue);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert entity to JSON string", e);
        }

        return responseDTO;
    }

    @Transactional
    public FrequentExpenseResponseDTO updateTemplate(Long templateId, FrequentExpenseRequestDTO request) {
        FrequentExpenseEntity template = frequentExpenseRepository.findById(templateId)
            .orElseThrow(() -> new ResourceNotFoundException("Frequent expense template not found"));

        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        if (currentUser != null && currentUser.isAuthenticated() 
            && currentUser.getPrincipal() instanceof CustomUserDetails userDetails) {
            if (!template.getUser().getId().equals(userDetails.getId())) {
                throw new SecurityException("You are not authorized to update this transaction");
            }
        } else {
            throw new SecurityException("Authentication information is missing or invalid");
        }

        FrequentExpenseResponseDTO previousTemplate = mapToResponseDTO(template);

        CategoryEntity category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        template.setCategory(category);
        template.setName(request.getName());
        template.setAmount(request.getAmount());

        FrequentExpenseEntity savedTemplate = frequentExpenseRepository.save(template);

        FrequentExpenseResponseDTO responseDTO = mapToResponseDTO(savedTemplate);

        try {
            String newValue = objectMapper.writeValueAsString(responseDTO);
            String previousValue = objectMapper.writeValueAsString(previousTemplate);
            historyService.appendEntry(userDetails.getId(), OBJECT_TYPE_FREQUENT_EXPENSE, savedTemplate.getId(), "UPDATE", previousValue, newValue);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert entity to JSON string", e);
        }

        return responseDTO;
    }

    public Boolean deleteTemplate(Long templateId) {
        FrequentExpenseEntity template = frequentExpenseRepository.findById(templateId)
            .orElseThrow(() -> new ResourceNotFoundException("Frequent expense template not found"));

        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        if (currentUser != null && currentUser.isAuthenticated() 
            && currentUser.getPrincipal() instanceof CustomUserDetails userDetails) {
            if (!template.getUser().getId().equals(userDetails.getId())) {
                throw new SecurityException("You are not authorized to delete this transaction");
            }
        } else {
            throw new SecurityException("Authentication information is missing or invalid");
        }

        FrequentExpenseResponseDTO previousTemplate = mapToResponseDTO(template);

        frequentExpenseRepository.delete(template);

        try { 
            String previousValue = objectMapper.writeValueAsString(previousTemplate);
            historyService.appendEntry(userDetails.getId(), OBJECT_TYPE_FREQUENT_EXPENSE, templateId, "DELETE", previousValue, null);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert entity to JSON string", e);
        }

        return true;
    }

    private FrequentExpenseResponseDTO mapToResponseDTO(FrequentExpenseEntity template) {
        FrequentExpenseResponseDTO dto = new FrequentExpenseResponseDTO();
        dto.setId(template.getId());
        if (template.getCategory() != null) {
            dto.setCategoryId(template.getCategory().getId());
            dto.setCategoryName(template.getCategory().getName());
        }
        dto.setName(template.getName());
        dto.setAmount(template.getAmount());
        return dto;
    }

    @Transactional(readOnly = true)
    public TemplateResponseDTO useTemplate(Long templateId) {
        FrequentExpenseEntity entity = frequentExpenseRepository.findById(templateId)
            .orElseThrow(() -> new ResourceNotFoundException("Frequent expense template not found"));

        TransactionResponseDTO draft = new TransactionResponseDTO();
        draft.setAmount(entity.getAmount());
        draft.setCategoryId(entity.getCategory().getId());
        draft.setCategoryName(entity.getCategory().getName());

        TemplateResponseDTO template = new TemplateResponseDTO();
        template.setTemplateId(templateId);
        template.setTemplateName(entity.getName());
        template.setDraft(draft);

        return template;
    }

}
