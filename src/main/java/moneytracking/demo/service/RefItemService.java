package moneytracking.demo.service;

import org.springframework.stereotype.Service;

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

}
