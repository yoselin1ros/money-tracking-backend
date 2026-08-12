package moneytracking.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import moneytracking.demo.dto.RefItemResponseDTO;
import moneytracking.demo.entity.RefItemEntity;
import moneytracking.demo.repository.RefItemRepository;

@Service
public class RefItemService {

    private final RefItemRepository refItemRepository;

    public RefItemService(RefItemRepository refItemRepository) {
        this.refItemRepository = refItemRepository;
    }

    public RefItemEntity findByNameAndCategory(Integer category, String itemName) {
        return refItemRepository.findByNameAndCategory(category, itemName);
    }

    public List<RefItemResponseDTO> findByRefCategoryId(Integer categoryId) {
        List<RefItemEntity> items = refItemRepository.findByRefCategoryId(categoryId);

        return items.stream().map(this::mapToResponseDTO).toList();
    }

    private RefItemResponseDTO mapToResponseDTO(RefItemEntity item) {
        RefItemResponseDTO dto = new RefItemResponseDTO();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());

        return dto;
    }

}
