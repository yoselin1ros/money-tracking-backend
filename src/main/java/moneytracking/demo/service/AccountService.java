package moneytracking.demo.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import moneytracking.demo.dto.AccountRequestDTO;
import moneytracking.demo.dto.AccountResponseDTO;
import moneytracking.demo.dto.CustomUserDetails;
import moneytracking.demo.entity.AccountEntity;
import moneytracking.demo.entity.RefItemEntity;
import moneytracking.demo.entity.UserEntity;
import moneytracking.demo.exception.ResourceNotFoundException;
import moneytracking.demo.repository.AccountRepository;
import moneytracking.demo.repository.RefItemRepository;
import moneytracking.demo.repository.TransactionRepository;
import moneytracking.demo.repository.UserRepository;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final RefItemRepository refItemRepository;
    private final TransactionRepository transactionRepository;

    public AccountService(
        AccountRepository accountRepository, UserRepository userRepository, RefItemRepository refItemRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.refItemRepository = refItemRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public AccountResponseDTO createAccount(AccountRequestDTO request) {
        UserEntity user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        RefItemEntity type = refItemRepository.findById(request.getTypeId())
            .orElseThrow(() -> new ResourceNotFoundException("Type not found"));
        
        AccountEntity account = new AccountEntity();
        account.setUser(user);
        account.setName(request.getName());
        account.setType(type);
        account.setInitialBalance(request.getInitialBalance());
        account.setCurrentBalance(request.getInitialBalance());

        AccountEntity savedAccount = accountRepository.save(account);
        return mapToResponseDTO(savedAccount);
    }

    @Transactional
    public AccountResponseDTO updateAccount(Long accountId, AccountRequestDTO request) {
        AccountEntity account = accountRepository.findById(accountId)
            .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() 
            && auth.getPrincipal() instanceof CustomUserDetails userDetails) {
            if (!account.getUser().getId().equals(userDetails.getId())) {
                throw new SecurityException("You are not authorized to update this account");
            }
        } else {
            throw new SecurityException("Authentication information is missing or invalid");
        }
        
        account.setName(request.getName());

        RefItemEntity type = refItemRepository.findById(request.getTypeId())
            .orElseThrow(() -> new ResourceNotFoundException("Type not found"));
        account.setType(type);

        AccountEntity updatedAccount = accountRepository.save(account);
        return mapToResponseDTO(updatedAccount);
    }

    @Transactional
    public Boolean deleteAccount(Long AccountId) {
        if (!accountRepository.existsById(AccountId)) {
            throw new ResourceNotFoundException("Account not found");
        }

        if (transactionRepository.existsByAccountId(AccountId)) {
            throw new DataIntegrityViolationException("Cannot delete account with associated transactions");
        }
        accountRepository.deleteById(AccountId);
        return true;
    }

    private AccountResponseDTO mapToResponseDTO(AccountEntity account) {
        AccountResponseDTO dto = new AccountResponseDTO();
        dto.setId(account.getId());
        dto.setName(account.getName());
        if (account.getType() != null) {
            dto.setTypeId(account.getType().getId());
            dto.setTypeName(account.getType().getName());
        }
        dto.setInitialBalance(account.getInitialBalance());
        dto.setCurrentBalance(account.getCurrentBalance());
        return dto;
    }

    @Transactional(readOnly = true)
    public List<AccountResponseDTO> getAccountsWithBalances(Long userId) {
        List<AccountEntity> accounts = accountRepository.findByUserIdOrderByIdAsc(userId);
        return accounts.stream()
            .map(this::mapToResponseDTO)
            .toList();
    }
}
