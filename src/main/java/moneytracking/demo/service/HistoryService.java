package moneytracking.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import moneytracking.demo.entity.HistoryLogEntity;
import moneytracking.demo.entity.RefItemEntity;
import moneytracking.demo.entity.UserEntity;
import moneytracking.demo.exception.ResourceNotFoundException;
import moneytracking.demo.repository.HistoryLogRepository;
import moneytracking.demo.repository.RefItemRepository;
import moneytracking.demo.repository.UserRepository;

@Service
public class HistoryService {
    private final HistoryLogRepository historyLogRepository;
    private final UserRepository userRepository;
    private final RefItemRepository refItemRepository;

    private static final int OBJECT_TYPE_CATEGORY = 7;
    private static final int ACTION_CATEGORY = 8;

    public HistoryService(HistoryLogRepository historyLogRepository, UserRepository userRepository,
            RefItemRepository refItemRepository) {
        this.historyLogRepository = historyLogRepository;
        this.userRepository = userRepository;
        this.refItemRepository = refItemRepository;
    }

    @Transactional
    public void appendEntry(Long userId, String objectType, Long objectId, String action,
            String previousValue, String newValue) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        RefItemEntity objectTypeRef = refItemRepository.findByNameAndCategory(OBJECT_TYPE_CATEGORY, objectType);
        if (objectTypeRef == null) {
            throw new ResourceNotFoundException("Object type not found");
        }

        RefItemEntity actionRef = refItemRepository.findByNameAndCategory(ACTION_CATEGORY, action);
        if (actionRef == null) {
            throw new ResourceNotFoundException("Action not found");
        }

        HistoryLogEntity historyLog = new HistoryLogEntity();
        historyLog.setUser(user);
        historyLog.setObjectType(objectTypeRef);
        historyLog.setObjectId(objectId);
        historyLog.setAction(actionRef);
        historyLog.setPreviousValue(previousValue);
        historyLog.setNewValue(newValue);

        // Save the history log entry to the database
        historyLogRepository.save(historyLog);
    }
}
