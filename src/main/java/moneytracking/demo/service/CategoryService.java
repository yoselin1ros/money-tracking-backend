package moneytracking.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import moneytracking.demo.dto.CategoryResponseDTO;
import moneytracking.demo.entity.CategoryEntity;
import moneytracking.demo.repository.CategoryRepository;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> listCategories(Long userId) {
        return categoryRepository.listCategoriesByUserId(userId).stream()
            .map(this::mapToResponseDTO)
            .collect(Collectors.toList());
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
