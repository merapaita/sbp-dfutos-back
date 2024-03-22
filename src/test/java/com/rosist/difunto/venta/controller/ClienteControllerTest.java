package com.rosist.difunto.venta.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rosist.difunto.venta.model.Cliente;
import com.rosist.difunto.venta.service.IClienteService;

@WebMvcTest
public class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IClienteService service;

    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void testGuardarCliente() throws Exception {		// 
        //given
        Cliente cliente = Cliente.builder()
            	.codcli(4)
            	.tipdoccli("1")
            	.doccli("01010103")
            	.nomcli("JUAN PEREZ")
            	.dircli("PAITA")
                .build();
        given(service.registraTransaccion(any(Cliente.class)))
                .willAnswer((invocation) -> invocation.getArgument(0));

        //when
        ResultActions response = mockMvc.perform(post("/clienteVenta/registrar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cliente)));

        //then
        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nomcli",is(cliente.getNomcli())))
                .andExpect(jsonPath("$.dircli",is(cliente.getDircli())))
                .andExpect(jsonPath("$.doccli",is(cliente.getDoccli())));
    }

}
