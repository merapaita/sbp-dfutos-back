package com.rosist.difunto.venta.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import com.rosist.difunto.exception.ModelNotFoundException;
import com.rosist.difunto.exception.ResourceNotFoundException;
import com.rosist.difunto.venta.model.Cliente;
import com.rosist.difunto.venta.repo.IClienteRepo;
import com.rosist.difunto.venta.service.impl.ClienteServiceImpl;

@ExtendWith(MockitoExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ClienteServiceTest {

    @Mock
    private IClienteRepo repo;
    
    @InjectMocks
    private ClienteServiceImpl service;

    private Cliente cliente;
    
    @BeforeEach
    void setup(){
        cliente = Cliente.builder()
                .codcli(4)
            	.tipdoccli("1")
            	.doccli("01010103")
            	.nomcli("JUAN PEREZ")
            	.dircli("PAITA")
                .build();
    }
    
//    @DisplayName("Test para guardar un empleado")
//    @Test
//    void testGuardarCliente() throws SQLException, Exception{
//        //given
//        given(repo.findById(cliente.getCodcli()))
//                .willReturn(Optional.empty());
//        given(repo.save(cliente)).willReturn(cliente);
//
//        //when
//        Cliente clienteGuardado = service.registraTransaccion(cliente);
//
//        //then
//        assertThat(clienteGuardado).isNotNull();
//    }
    
//    @DisplayName("Test para guardar un empleado con Throw Exception")
//    @Test
//    void testGuardarClienteConThrowException(){
//        //given
//        given(repo.findById(cliente.getCodcli()))
//                .willReturn(Optional.of(cliente));
//        //when
//        assertThrows(ModelNotFoundException.class,() -> {
//           service.registraTransaccion(cliente);
//        });
//
//        //then
//        verify(repo,never()).save(any(Cliente.class));
//    }

    @DisplayName("Test para listar a los empleados")
    @Test
    void testListarEmpleados() throws Exception{
        //given
        Cliente cliente1 = Cliente.builder()
                .codcli(5)
            	.tipdoccli("1")
            	.doccli("01010104")
            	.nomcli("JUAN PEREZ")
            	.dircli("PAITA")
                .build();
        given(repo.findAll()).willReturn(List.of(cliente,cliente1));

        //when
        List<Cliente> empleados = service.listar();

        //then
        assertThat(empleados).isNotNull();
        assertThat(empleados.size()).isEqualTo(2);
    }
    
}
