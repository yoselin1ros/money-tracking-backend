package moneytracking.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import moneytracking.demo.dto.CategoryRequestDTO;
import moneytracking.demo.dto.CategoryResponseDTO;
import moneytracking.demo.entity.CategoryEntity;
import moneytracking.demo.entity.RefItemEntity;
import moneytracking.demo.entity.UserEntity;
import moneytracking.demo.exception.ResourceNotFoundException;
import moneytracking.demo.repository.CategoryRepository;
import moneytracking.demo.repository.RefItemRepository;
import moneytracking.demo.repository.UserRepository;
import moneytracking.demo.repository.TransactionRepository;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final RefItemRepository refItemRepository;
    private final TransactionRepository transactionRepository;

    public CategoryService(
        CategoryRepository categoryRepository, UserRepository userRepository, RefItemRepository refItemRepository,
        TransactionRepository transactionRepository
    ) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.refItemRepository = refItemRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public CategoryResponseDTO createCategory(CategoryRequestDTO request) {
        // 1. Fetch references to target relationship
        UserEntity user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        RefItemEntity type = refItemRepository.findById(request.getTypeId())
            .orElseThrow(() -> new ResourceNotFoundException("Type not found"));

        // 1.1 Check if a category with the same name already exists for the user
        if (categoryRepository.existsByNameAndUserId(request.getName(), request.getUserId())) {
            throw new IllegalArgumentException("Category with the same name already exists for this user");
        }

        // 2. Map Request DTO to Entity
        CategoryEntity category = new CategoryEntity();
        category.setName(request.getName());
        category.setUser(user);
        category.setType(type);

        CategoryEntity savedCategory = categoryRepository.save(category);

        // 3. Return mapped Response DTO
        return mapToResponseDTO(savedCategory);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> listCategories(Long userId) {
        return categoryRepository.listCategoriesByUserId(userId).stream()
            .map(this::mapToResponseDTO)
            .collect(Collectors.toList());
    }

    @Transactional
    public CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO request) {
        CategoryEntity category = categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        // Check if a category with the same name already exists for the user
        if (categoryRepository.existsByNameAndUserId(request.getName(), request.getUserId()) && 
            !category.getName().equals(request.getName())) {
            throw new IllegalArgumentException("Category with the same name already exists for this user");
        }

        category.setName(request.getName());

        RefItemEntity type = refItemRepository.findById(request.getTypeId())
            .orElseThrow(() -> new ResourceNotFoundException("Type not found"));
        category.setType(type);

        CategoryEntity updatedCategory = categoryRepository.save(category);
        return mapToResponseDTO(updatedCategory);
    }

    @Transactional
    public Boolean deleteCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category not found");
        }

        if (transactionRepository.existByCategoryId(categoryId)) {
            throw new DataIntegrityViolationException("Cannot delete category with associated transactions");
        }

        categoryRepository.deleteById(categoryId);
        return true;
    }

    private CategoryResponseDTO mapToResponseDTO(CategoryEntity category) {
        CategoryResponseDTO dto = new CategoryResponseDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        if (category.getType() != null) {
            dto.setTypeId(category.getType().getId());
            dto.setTypeName(category.getType().getName());
        }
        return dto;
    }
}
