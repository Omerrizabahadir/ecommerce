package com.cornershop.ecommerce.service;

import com.cornershop.ecommerce.exception.CategoryDeleteException;
import com.cornershop.ecommerce.exception.CategoryDuplicateException;
import com.cornershop.ecommerce.exception.CategoryNotFoundException;
import com.cornershop.ecommerce.model.Category;
import com.cornershop.ecommerce.repository.CategoryRepository;
import com.cornershop.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class CategoryServiceTest {

    @InjectMocks                                    //test etmek istediğin yeri injectMock ile göster.CategoryService sınıfının bir örneği oluşturulacak ve bu örnekteki bağımlılıklar (CategoryRepository ve ProductRepository) otomatik olarak mock nesneleri ile sağlanacaktır.
    private  CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @BeforeEach                                     //Bu anotasyon, JUnit testlerinin her biri çalışmadan önce setUp metodunun çağrılmasını sağlar. Bu metod, testlerinizi çalıştırmadan önce gerekli hazırlıkları yapar.
    public void  setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createCategory_succesfull(){
        Category category = new Category();
        category.setName("TEST_CATEGORY");

        Category savedCategory = new Category();
        savedCategory.setName(category.getName());
        savedCategory.setId(1L);

        when(categoryRepository.findCategoryByName(category.getName())).thenReturn(Optional.empty());    // //findCategoryByName'i "TEST_CATEGORY" ile çağırdığımda empty dön diyoruz
        when(categoryRepository.save(category)).thenReturn(savedCategory);                                      //category 'yi id'si null name 'i "TEST CATEGORY" çağırınca savedCategory dön

        Category response = categoryService.createCategory(category);

       assertEquals(category.getName(), response.getName());                                                    // category.getName()-->expected, result-->response.getName()
       verify(categoryRepository,times(1)).findCategoryByName(category.getName());      //findCategoryByName'i 1 kez çağırdın mı
       verify(categoryRepository,times(1)).save(category);                              //save 'i 1 kez çağırdın mı
    }
    @Test
    public void createCategory_fail(){
        Category category = new Category();
        category.setName("TEST CATEGORY");

        Category savedCategory = new Category();
        savedCategory.setName(category.getName());
        savedCategory.setId(1L);

        //ben dönmesini istediğim neyse onu buraya-->findCategoryByName(category.getName())) yazarım.
        when(categoryRepository.findCategoryByName(category.getName())).thenReturn(Optional.ofNullable(savedCategory));   //findCategoryByName'i "TEST CATEGORY" gelirse  -->savedCategory 'yi dön

        CategoryDuplicateException thrown = assertThrows(CategoryDuplicateException.class,
                () -> categoryService.createCategory(category));                                                           //category yollayarak-->"TEST CATEGORY" 'yi yollamış olduk. "TEST CATEGORY" gelirse doğru bunun dışında ne gelirse gelsin (küçük harfli boşluğu farklı veya fazladan harfli veya eksik harfli) hepsini yanlış kabul eder

        assertEquals("Category is already defined : " + category.getName(), thrown.getMessage());
        verify(categoryRepository, times(1)).findCategoryByName(category.getName());
        verify(categoryRepository,times(0)).save(category);                             //başarız olacağı için save metodunu çağıramaz. bu nedenle 0 kez çağrılacak dedik
    }

    @Test
    void deleteCategory_success(){
        Long categoryId = 3L;

        when(productRepository.getProductCountOfCategoryId(categoryId)).thenReturn(0L);  //getProductCountOfCategoryId(categoryId) 3 idi, categoryId=0 dönerse

        //To test void method, use doNothing because it doesn't return any data.
        Mockito.doNothing().when(categoryRepository).deleteById(categoryId);    //burada deleteById ile id yi siliyor.hiçbir şey dönmeyecek .O zaman thenReturn() kısmı olmayacak

        categoryService.deleteCategory(categoryId);

        //assertEquals olmaz bir şey dönmediği için
        verify(productRepository,times(1)).getProductCountOfCategoryId(categoryId);   //yanlış test etmesi için getProductCountOfCategoryId(categoryId) -> 2L yaz categoryId yi 3l beklediği için hata verir
        verify(categoryRepository,times(1)).deleteById(categoryId);
    }

    @Test
    void deleteCategory_fail() {
        Long categoryId = 3L;

        when(productRepository.getProductCountOfCategoryId(categoryId)).thenReturn(1L);

        CategoryDeleteException thrown = Assertions.assertThrows(CategoryDeleteException.class,
                () -> categoryService.deleteCategory(categoryId));

        assertEquals("you can not delete this category because category has 1 products",thrown.getMessage());
        verify(productRepository, times(1)).getProductCountOfCategoryId(categoryId);
        verify(categoryRepository, times(0)).deleteById(categoryId);
    }
    //TODO: CategoryService için kalan methodların testini yaz.

    @Test
    void getCategory_successful(){
        Long categoryId = 1L;
        Category category = new Category();
        category.setId(categoryId);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        Category response = categoryService.getCategory(categoryId);

        assertEquals(category.getId(), response.getId());
        verify(categoryRepository,times(1)).findById(categoryId);

    }
    @Test
    void getCategory_fail(){
        Long categoryId = 1L;

        //categoryId olmayacağı için set edemez ve hata atar
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        CategoryNotFoundException thrown =  assertThrows(CategoryNotFoundException.class,
                () -> categoryService.getCategory(categoryId));


        verify(categoryRepository, times(1)).findById(categoryId);

    }
    @Test
    void getAllCategoryList(){

        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("Category Test1");

        Category category2 = new Category();
        category2.setId(1L);
        category2.setName("Category Test2");

        List <Category> categories = Arrays.asList(category1,category2);

        when(categoryRepository.findAll()).thenReturn(categories);

        List <Category> respond = categoryService.getAllCategoryList();

        assertEquals(2,respond.size());
        verify(categoryRepository,times(1)).findAll();

    }
    @Test
    void updateCategory(){
        Category category = new Category();
        category.setId(1L);
        category.setName("Category Test");

        Category updatedCategory = new Category();
        updatedCategory.setId(1L);
        updatedCategory.setName("Category Test");

        when(categoryRepository.save(category)).thenReturn(category);

        Category response = categoryService.updateCategory(category);

        assertEquals(category.getId(),response.getId());
        assertEquals(category.getName(),response.getName());
        verify(categoryRepository,times(1)).save(category);
    }
}

