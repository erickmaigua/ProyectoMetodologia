package com.zapateria.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "usuarios")
public class Usuario {
    @Id
    private String id;
    private String nombre;
    private String apellido;
    private String email;
    private String username;
    private String password;
    private String telefono;
    private String direccion;
    private String rol; // "CLIENTE", "EMPLEADO" o "ADMINISTRADOR"
    
    // Nuevo campo: Lista de IDs de clientes asignados (solo para EMPLEADO)
    private List<String> clientesAsignados;
    
    // Nuevo campo: ID del empleado que gestiona este cliente (solo para CLIENTE)
    private String empleadoAsignadoId;
    
    public Usuario() {
        this.clientesAsignados = new ArrayList<>();
    }
    
    // Getters y Setters existentes...
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    
    // Nuevos getters y setters
    public List<String> getClientesAsignados() { 
        return clientesAsignados != null ? new ArrayList<>(clientesAsignados) : null; 
    }
    public void setClientesAsignados(List<String> clientesAsignados) { 
        this.clientesAsignados = clientesAsignados != null ? new ArrayList<>(clientesAsignados) : null; 
    }
    
    public String getEmpleadoAsignadoId() { return empleadoAsignadoId; }
    public void setEmpleadoAsignadoId(String empleadoAsignadoId) { 
        this.empleadoAsignadoId = empleadoAsignadoId; 
    }
}