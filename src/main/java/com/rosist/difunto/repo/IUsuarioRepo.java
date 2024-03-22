package com.rosist.difunto.repo;

import com.rosist.difunto.model.Usuario;
import com.rosist.difunto.venta.repo.IGenericRepo;

public interface IUsuarioRepo extends IGenericRepo<Usuario, Integer>  {

	//from usuario where username = ?
	Usuario findOneByUsername(String username);	
}
