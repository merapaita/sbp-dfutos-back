package com.rosist.difunto.venta.repo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.rosist.difunto.venta.model.Cliente;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class IClienteRepoTest {

	@Autowired
    private IClienteRepo repo;
	
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
	
    @DisplayName("Test para guardar un cliente")
	@Test
	void testGuardarCliente() {

		Cliente clienteGuardado = repo.save(cliente);
		
		assertThat(clienteGuardado).isNotNull();
		assertThat(clienteGuardado.getCodcli()).isGreaterThan(0);
		assertThat(true);
	}
	
    @DisplayName("Test para listar clientes")
    @Test
    void testListarCliente(){
        //given
//        Empleado empleado1 = Empleado.builder()
//                .nombre("Julen")
//                .apellido("Oliva")
//                .email("j2@gmail.com")
//                .build();
        repo.save(cliente);

        //when
        List<Cliente> listaClientes = repo.findAll();

        //then
        assertThat(listaClientes).isNotNull();
        assertThat(listaClientes.size()).isEqualTo(4);
    }

    @DisplayName("Test para obtener un cliente por ID")
    @Test
    void testObtenerClientePorId(){
        repo.save(cliente);

        //when - comportamiento o accion que vamos a probar
        Cliente clienteBuscado = repo.findById(cliente.getCodcli()).get();

        //then
        assertThat(clienteBuscado).isNotNull();
    }
    
    @DisplayName("Test para actualizar un cliente")
    @Test
    void testActualizarEmpleado(){
        repo.save(cliente);

        //when
        Cliente clienteGuardado = repo.findById(cliente.getCodcli()).get();
        clienteGuardado.setNomcli("Juan Perez Modificado");
        Cliente clienteActualizado = repo.save(clienteGuardado);

        //then
        assertThat(clienteActualizado.getNomcli()).isEqualTo("Juan Perez Modificado");
    }

    @DisplayName("Test para eliminar un cliente")
    @Test
    void testEliminarCliente(){
        repo.save(cliente);

        //when
        repo.deleteById(cliente.getCodcli());
        Optional<Cliente> empleadoOptional = repo.findById(cliente.getCodcli());

        //then
        assertThat(empleadoOptional).isEmpty();
    }
    
}
