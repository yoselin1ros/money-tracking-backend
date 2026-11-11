package moneytracking.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import moneytracking.demo.dto.CategoryRequestDTO;
import moneytracking.demo.dto.CategoryResponseDTO;
import moneytracking.demo.dto.CustomUserDetails;
import moneytracking.demo.entity.CategoryEntity;
import moneytracking.demo.entity.RefCategoryEntity;
import moneytracking.demo.entity.RefItemEntity;
import moneytracking.demo.entity.UserEntity;
import moneytracking.demo.repository.CategoryRepository;
import moneytracking.demo.repository.RefItemRepository;
import moneytracking.demo.repository.TransactionRepository;
import moneytracking.demo.repository.UserRepository;


@ExtendWith (MockitoExtension.class) // Initializes Mockito annotations without starting Spring
public class CategoryServiceTest {

    @Mock 
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefItemRepository refItemRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private HistoryService historyService;

    @InjectMocks 
    private CategoryService categoryService;

    @Test 
    void createCategory_ShouldReturnSavedCategoryResponseDTO() {
        // Arrange
        CategoryRequestDTO inputDto = new CategoryRequestDTO();
        inputDto.setName("Health");
        inputDto.setDescription("Health-related expenses");
        inputDto.setTypeId(5L);
        inputDto.setUserId(1L);

        RefCategoryEntity mockRefCategory = new RefCategoryEntity();
        mockRefCategory.setId(1L);
        mockRefCategory.setName("flow_type");
        mockRefCategory.setDescription("Income or expense of money");

        RefItemEntity mockType = new RefItemEntity();
        mockType.setId(5L);
        mockType.setRefCategory(mockRefCategory);
        mockType.setName("expense");
        mockType.setDescription("Expense made");

        UserEntity mockUser = new UserEntity();
        mockUser.setId(1L);
        mockUser.setEmail("email@example.com");
        mockUser.setPasswordHash("hashedPassword");
        mockUser.setDisplayName("TestUser");

        CategoryEntity mockSavedCategory = new CategoryEntity();
        mockSavedCategory.setId(1L);
        mockSavedCategory.setName("Health");
        mockSavedCategory.setDescription("Health-related expenses");
        mockSavedCategory.setType(mockType);
        mockSavedCategory.setUser(mockUser);

        // relational lookup mocks
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        Mockito.when(refItemRepository.findById(5L)).thenReturn(Optional.of(mockType));

        // Mock repository behavior
        Mockito.when(categoryRepository.save(any(CategoryEntity.class))).thenReturn(mockSavedCategory);

        // Act
        CategoryResponseDTO result = categoryService.createCategory(inputDto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Health", result.getName());
        assertEquals("Health-related expenses", result.getDescription());

        // Verify all parts of the relationship chain were utilized
        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(refItemRepository, Mockito.times(1)).findById(5L);
        Mockito.verify(categoryRepository, Mockito.times(1)).save(any(CategoryEntity.class));
    }

    @Test
    void updateCategory_WhenUserIsOwner_ShouldReturnUpdatedCategoryResponseDTO() {
        // 1. Arrange: Mock the logged-in user
        UserEntity owner = new UserEntity();
        owner.setId(1L);
        owner.setEmail("owner@example.com");
        owner.setPasswordHash("hashedPassword");
        owner.setDisplayName("TestUser");
        mockAuthenticatedUser(owner);

        RefCategoryEntity mockRefCategory = new RefCategoryEntity();
        mockRefCategory.setId(1L);
        mockRefCategory.setName("flow_type");
        mockRefCategory.setDescription("Income or expense of money");
 
        RefItemEntity existingType = new RefItemEntity();
        existingType.setId(5L);
        existingType.setRefCategory(mockRefCategory);
        existingType.setName("expense");
        existingType.setDescription("Expense made");

        RefItemEntity newType = new RefItemEntity();
        newType.setId(55L);
        newType.setRefCategory(mockRefCategory);
        newType.setName("income");
        newType.setDescription("Income of money");

        CategoryEntity existingCategory = new CategoryEntity();
        existingCategory.setId(1L);
        existingCategory.setName("Health");
        existingCategory.setDescription("Health-related expenses");
        existingCategory.setType(existingType);
        existingCategory.setUser(owner);

        CategoryRequestDTO updatedRequest = new CategoryRequestDTO();
        updatedRequest.setName("Beauty");
        updatedRequest.setDescription("Beauty-related expenses");
        updatedRequest.setTypeId(newType.getId());
        updatedRequest.setUserId(owner.getId());

        CategoryEntity updatedCategory = new CategoryEntity();
        updatedCategory.setId(1L);
        updatedCategory.setName("Beauty");
        updatedCategory.setDescription("Beauty-related expenses");
        updatedCategory.setType(newType);
        updatedCategory.setUser(owner);

        // Stub repository lookups
        Mockito.when(categoryRepository.findById(1L)).thenReturn(Optional.of(existingCategory));
        Mockito.when(refItemRepository.findById(55L)).thenReturn(Optional.of(newType));
        Mockito.when(categoryRepository.save(any(CategoryEntity.class))).thenReturn(updatedCategory);

        // Act
        CategoryResponseDTO result = categoryService.updateCategory(1L, updatedRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Beauty", result.getName());
        assertEquals("Beauty-related expenses", result.getDescription());
        Mockito.verify(categoryRepository).save(any(CategoryEntity.class));
    }

    @Test
    void updateCategory_WhenUserIsNotOwner_ShouldThrowException() {
        // 1. Arrange: Logged-in user is a hacker/different user
        UserEntity hacker = new UserEntity();
        hacker.setId(100L);
        hacker.setEmail("hacker@example.com");
        hacker.setPasswordHash("hashedPassword");
        hacker.setDisplayName("Hacker");
        mockAuthenticatedUser(hacker);

        UserEntity actualOwner = new UserEntity();
        actualOwner.setId(1L);
        actualOwner.setEmail("owner@example.com");
        actualOwner.setPasswordHash("hashedPassword");
        actualOwner.setDisplayName("TestUser");

        RefCategoryEntity mockRefCategory = new RefCategoryEntity();
        mockRefCategory.setId(1L);
        mockRefCategory.setName("flow_type");
        mockRefCategory.setDescription("Income or expense of money");

        RefItemEntity existingType = new RefItemEntity();
        existingType.setId(5L);
        existingType.setRefCategory(mockRefCategory);
        existingType.setName("expense");
        existingType.setDescription("Expense made");

        CategoryEntity existingCategory = new CategoryEntity();
        existingCategory.setId(1L);
        existingCategory.setName("Health");
        existingCategory.setDescription("Health-related expenses");
        existingCategory.setType(existingType);
        existingCategory.setUser(actualOwner);

        CategoryRequestDTO updatedRequest = new CategoryRequestDTO();
        updatedRequest.setName("Beauty");
        updatedRequest.setDescription("Beauty-related expenses");
        updatedRequest.setTypeId(existingType.getId());
        updatedRequest.setUserId(hacker.getId());

        Mockito.when(categoryRepository.findById(1L)).thenReturn(Optional.of(existingCategory));

        // 2. Act & Assert 
        assertThrows(RuntimeException.class, () -> {
            categoryService.updateCategory(1L, updatedRequest);
        });

        // Ensuring save will NEVER called
        Mockito.verify(categoryRepository, Mockito.never()).save(any(CategoryEntity.class));
    }

    @Test
    void deleteCategory_WhenUserIsOwner_ShouldDeleteSuccessfully() {
        // 1. Arrange
        UserEntity owner = new UserEntity();
        owner.setId(1L);
        owner.setEmail("owner@example.com");
        owner.setPasswordHash("hashedPassword");
        owner.setDisplayName("TestUser");
        mockAuthenticatedUser(owner);

        RefCategoryEntity mockRefCategory = new RefCategoryEntity();
        mockRefCategory.setId(1L);
        mockRefCategory.setName("flow_type");
        mockRefCategory.setDescription("Income or expense of money");

        RefItemEntity existingType = new RefItemEntity();
        existingType.setId(5L);
        existingType.setRefCategory(mockRefCategory);
        existingType.setName("expense");
        existingType.setDescription("Expense made");

        CategoryEntity existingCategory = new CategoryEntity();
        existingCategory.setId(1L);
        existingCategory.setName("Health");
        existingCategory.setDescription("Health-related expenses");
        existingCategory.setType(existingType);
        existingCategory.setUser(owner);

        Mockito.when(categoryRepository.findById(1L)).thenReturn(Optional.of(existingCategory));

        // 2. Act
        categoryService.deleteCategory(1L);

        // 3. Assert 
        Mockito.verify(categoryRepository, Mockito.times(1)).delete(existingCategory);
    }

    @Test
    void getCategoriesByUserId_ShouldReturnListOfCategoryResponseDTO() {
        // 1. Arrange
        Long userId = 1L;

        UserEntity owner = new UserEntity();
        owner.setId(1L);
        owner.setEmail("owner@example.com");
        owner.setPasswordHash("hashedPassword");
        owner.setDisplayName("TestUser");

        RefCategoryEntity mockRefCategory = new RefCategoryEntity();
        mockRefCategory.setId(1L);
        mockRefCategory.setName("flow_type");
        mockRefCategory.setDescription("Income or expense of money");

        RefItemEntity existingType = new RefItemEntity();
        existingType.setId(5L);
        existingType.setRefCategory(mockRefCategory);
        existingType.setName("expense");
        existingType.setDescription("Expense made");

        CategoryEntity cat1 = new CategoryEntity();
        cat1.setId(10L);
        cat1.setName("Food");
        cat1.setUser(owner);
        cat1.setType(existingType);

        CategoryEntity cat2 = new CategoryEntity();
        cat2.setId(11L);
        cat2.setName("Rent");
        cat2.setUser(owner);
        cat2.setType(existingType);

        // Stub user existence check 
        Mockito.when(categoryRepository.listCategoriesByUserId(userId)).thenReturn(List.of(cat1, cat2));

        // 2. Act
        List<CategoryResponseDTO> results = categoryService.listCategories(userId);

        // 3. Assert
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals("Food", results.get(0).getName());
        assertEquals("Rent", results.get(1).getName());
        Mockito.verify(categoryRepository, Mockito.times(1)).listCategoriesByUserId(userId);
    }

    // helper method to mock authenticated user
    private void mockAuthenticatedUser(UserEntity user) {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);

        Mockito.when(authentication.isAuthenticated()).thenReturn(true);
        Mockito.when(authentication.getPrincipal()).thenReturn(new CustomUserDetails(user)); 
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }

    // To prevent the static security context from leaking into other tests
    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }
}
