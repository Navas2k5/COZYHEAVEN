package com.springboot.cozyheavenstay.service;

import com.springboot.cozyheavenstay.dto.request.CustomerRequestDto;
import com.springboot.cozyheavenstay.dto.request.CustomerUpdateRequestDto;
import com.springboot.cozyheavenstay.dto.response.CustomerProfileResponseDto;
import com.springboot.cozyheavenstay.exception.InvalidCallException;
import com.springboot.cozyheavenstay.exception.ResourceNotFoundException;
import com.springboot.cozyheavenstay.model.Customer;
import com.springboot.cozyheavenstay.model.User;
import com.springboot.cozyheavenstay.repository.CustomerRepository;
import com.springboot.cozyheavenstay.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {
    @InjectMocks
    private CustomerService customerService;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    public void addCustomerTest(){
        CustomerRequestDto dto = new CustomerRequestDto(
                "testCustomer",
                "test@gmail.com",
                "9876543210",
                "Male",
                "Chennai",
                "testuser",
                "password"
        );

        User user = new User();

        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        customerService.addCustomer(dto);

        verify(passwordEncoder,times(1)).encode("password");
        verify(userRepository,times(1)).save(any(User.class));
        verify(customerRepository,times(1)).save(any(Customer.class));

        verifyNoMoreInteractions(passwordEncoder);
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(customerRepository);
    }

    @Test
    public void getCustomerProfileTest(){
        CustomerProfileResponseDto dto = new CustomerProfileResponseDto(
                1L,
                "testCustomer",
                "test@gmail.com",
                "9876543210",
                "Male",
                "Chennai",
                "testuser"
        );

        when(customerRepository.findCustomerProfileByUsername("testuser"))
                .thenReturn(Optional.of(dto));

        CustomerProfileResponseDto response = customerService.getCustomerProfile("testuser");

        assertEquals("testCustomer",response.name());
        assertEquals("test@gmail.com",response.email());
        assertEquals("testuser",response.username());

        verify(customerRepository,times(1)).findCustomerProfileByUsername("testuser");

        verifyNoMoreInteractions(customerRepository);
    }

    @Test
    public void getCustomerProfileTest_NotFound(){
        when(customerRepository.findCustomerProfileByUsername("wronguser"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                ()-> customerService.getCustomerProfile("wronguser"));

        verify(customerRepository,times(1)).findCustomerProfileByUsername("wronguser");

        verifyNoMoreInteractions(customerRepository);
    }

    @Test
    public void updateCustomerProfileTest(){
        User user = new User();
        user.setUsername("testuser");

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("oldName");
        customer.setEmail("old@gmail.com");
        customer.setContact("9876543210");
        customer.setGender("Male");
        customer.setAddress("Chennai");
        customer.setUser(user);

        CustomerUpdateRequestDto dto = new CustomerUpdateRequestDto(
                "newName",
                "new@gmail.com",
                "9876543211",
                "Male",
                "Bangalore"
        );

        when(customerRepository.getByUsername("testuser")).thenReturn(customer);
        when(customerRepository.existsByEmailAndIdNot("new@gmail.com",1L)).thenReturn(false);
        when(customerRepository.existsByContactAndIdNot("9876543211",1L)).thenReturn(false);

        CustomerProfileResponseDto response = customerService.updateCustomerProfile("testuser",dto);

        assertEquals("newName",response.name());
        assertEquals("new@gmail.com",response.email());
        assertEquals("9876543211",response.contact());
        assertEquals("Bangalore",response.address());

        verify(customerRepository,times(1)).getByUsername("testuser");
        verify(customerRepository,times(1)).existsByEmailAndIdNot("new@gmail.com",1L);
        verify(customerRepository,times(1)).existsByContactAndIdNot("9876543211",1L);

        verifyNoMoreInteractions(customerRepository);
    }

    @Test
    public void updateCustomerProfileTest_NotFound(){
        CustomerUpdateRequestDto dto = new CustomerUpdateRequestDto(
                "newName",
                "new@gmail.com",
                "9876543211",
                "Male",
                "Bangalore"
        );

        when(customerRepository.getByUsername("wronguser")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class,
                ()-> customerService.updateCustomerProfile("wronguser",dto));

        verify(customerRepository,times(1)).getByUsername("wronguser");

        verifyNoMoreInteractions(customerRepository);
    }

    @Test
    public void updateCustomerProfileTest_DuplicateEmail(){
        Customer customer = new Customer();
        customer.setId(1L);

        CustomerUpdateRequestDto dto = new CustomerUpdateRequestDto(
                "newName",
                "existing@gmail.com",
                "9876543211",
                "Male",
                "Bangalore"
        );

        when(customerRepository.getByUsername("testuser")).thenReturn(customer);
        when(customerRepository.existsByEmailAndIdNot("existing@gmail.com",1L)).thenReturn(true);

        assertThrows(InvalidCallException.class,
                ()-> customerService.updateCustomerProfile("testuser",dto));

        verify(customerRepository,times(1)).getByUsername("testuser");
        verify(customerRepository,times(1)).existsByEmailAndIdNot("existing@gmail.com",1L);
        verify(customerRepository,times(0)).existsByContactAndIdNot(anyString(),anyLong());

        verifyNoMoreInteractions(customerRepository);
    }

    @Test
    public void updateCustomerProfileTest_DuplicateContact(){
        Customer customer = new Customer();
        customer.setId(1L);

        CustomerUpdateRequestDto dto = new CustomerUpdateRequestDto(
                "newName",
                "new@gmail.com",
                "9876543210",
                "Male",
                "Bangalore"
        );

        when(customerRepository.getByUsername("testuser")).thenReturn(customer);
        when(customerRepository.existsByEmailAndIdNot("new@gmail.com",1L)).thenReturn(false);
        when(customerRepository.existsByContactAndIdNot("9876543210",1L)).thenReturn(true);

        assertThrows(InvalidCallException.class,
                ()-> customerService.updateCustomerProfile("testuser",dto));

        verify(customerRepository,times(1)).getByUsername("testuser");
        verify(customerRepository,times(1)).existsByEmailAndIdNot("new@gmail.com",1L);
        verify(customerRepository,times(1)).existsByContactAndIdNot("9876543210",1L);

        verifyNoMoreInteractions(customerRepository);
    }
}
