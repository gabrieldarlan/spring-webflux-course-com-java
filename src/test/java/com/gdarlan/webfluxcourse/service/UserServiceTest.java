package com.gdarlan.webfluxcourse.service;

import com.gdarlan.webfluxcourse.entity.User;
import com.gdarlan.webfluxcourse.mapper.UserMapper;
import com.gdarlan.webfluxcourse.model.request.UserRequest;
import com.gdarlan.webfluxcourse.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Objects;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;
    @Mock
    private UserMapper mapper;
    @InjectMocks
    private UserService service;

    @Test
    void must_be_save_a_user_with_successfully() {

        UserRequest request = new UserRequest("gabriel", "gabriel@gmail.com", "1234");
        User entity = User.builder().build();

        when(mapper.toEntity(any(UserRequest.class))).thenReturn(entity);
        when(repository.save(any(User.class))).thenReturn(Mono.just(User.builder().build()));

        Mono<User> result = service.save(request);

        StepVerifier.create(result)//! sobrescreve o metodo para que ele possa ser executado
                .expectNextMatches(Objects::nonNull)//! verifica o teste
                .expectComplete()//! se foi completado
                .verify(); //! verifica se todas as condições foram satisfeitas


        verify(repository, times(1)).save(any(User.class));//! verifica quantas vezes o metodo foi chamado
    }

    @Test
    void test_find_by_id_with_successfully() {
        when(repository.findById(anyString())).thenReturn(Mono.just(User.builder().id("123").build()));

        Mono<User> result = service.findById("123");

        StepVerifier.create(result)
                .expectNextMatches(user -> user.getClass() == User.class
                        && Objects.equals(user.getId(), "123"))
                .expectComplete()
                .verify();

        verify(repository, times(1)).findById(anyString());
    }

}