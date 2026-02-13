package com.zapateria.utils;

/**
 * Constantes globales de la aplicación.
 * Corrige PMD: AvoidDuplicateLiterals — strings repetidos en múltiples archivos.
 */
public final class Constants {

    private Constants() {}

    // ===== Respuestas generales =====
    public static final String SUCCESS    = "success";
    public static final String MENSAJE    = "mensaje";
    public static final String PEDIDO_KEY = "pedido";

    // ===== Mensajes de error comunes =====
    public static final String ID_INVALIDO           = "ID inválido";
    public static final String PEDIDO_NO_ENCONTRADO  = "Pedido no encontrado";
    public static final String PRODUCTO_NO_ENCONTRADO = "Producto no encontrado";
    public static final String USUARIO_NO_ENCONTRADO = "Usuario no encontrado";
    public static final String DATOS_INCOMPLETOS     = "Datos incompletos";

    // ===== Estados de pedido =====
    public static final String ESTADO_PENDIENTE   = "PENDIENTE";
    public static final String ESTADO_PROCESANDO  = "PROCESANDO";
    public static final String ESTADO_ENVIADO     = "ENVIADO";
    public static final String ESTADO_ENTREGADO   = "ENTREGADO";
    public static final String ESTADO_CANCELADO   = "CANCELADO";

    // ===== Estados de despacho =====
    public static final String DESPACHO_PENDIENTE  = "PENDIENTE";
    public static final String DESPACHO_ASIGNADO   = "ASIGNADO";
    public static final String DESPACHO_EN_RUTA    = "EN_RUTA";
    public static final String DESPACHO_ENTREGADO  = "ENTREGADO";
    public static final String DESPACHO_INCIDENCIA = "INCIDENCIA";

    // ===== Estados de queja =====
    public static final String QUEJA_ABIERTA  = "ABIERTA";
    public static final String QUEJA_RESUELTA = "RESUELTA";

    // ===== Roles =====
    public static final String ROL_CLIENTE        = "CLIENTE";
    public static final String ROL_EMPLEADO       = "EMPLEADO";
    public static final String ROL_ADMINISTRADOR  = "ADMINISTRADOR";
    public static final String ROL_DESPACHO       = "DESPACHO";

    // ===== Ubicaciones =====
    public static final String UBICACION_BODEGA    = "Bodega Principal";
    public static final String UBICACION_EN_RUTA   = "En ruta";
    public static final String UBICACION_ENTREGADO = "Entregado al cliente";
}
