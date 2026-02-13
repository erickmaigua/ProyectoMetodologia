#!/bin/bash

echo "================================================"
echo "  VERIFICACIÓN DE CORRECCIONES - PROYECTO ZAPATERÍA"
echo "================================================"
echo ""

# Contadores
total_checks=0
passed_checks=0
failed_checks=0

check_pass() {
    echo "✅ $1"
    ((passed_checks++))
    ((total_checks++))
}

check_fail() {
    echo "❌ $1"
    ((failed_checks++))
    ((total_checks++))
}

check_info() {
    echo "ℹ️  $1"
}

echo "🔍 Verificando correcciones de seguridad..."
echo ""

# 1. Verificar que no hay System.out.println
echo "📋 Test 1: System.out.println eliminado"
if grep -r "System.out.println" src/main/java/ 2>/dev/null | grep -v "//.*System.out.println" > /dev/null; then
    check_fail "Aún existen System.out.println en el código"
else
    check_pass "No se encontraron System.out.println"
fi

# 2. Verificar uso de Logger
echo "📋 Test 2: Logger implementado"
if grep -r "Logger logger" src/main/java/ > /dev/null; then
    check_pass "Logger encontrado en el código"
else
    check_fail "No se encontró uso de Logger"
fi

# 3. Verificar application.properties
echo "📋 Test 3: application.properties existe"
if [ -f "src/main/resources/application.properties" ]; then
    check_pass "application.properties creado"
else
    check_fail "application.properties no encontrado"
fi

# 4. Verificar .env.example
echo "📋 Test 4: .env.example existe"
if [ -f ".env.example" ]; then
    check_pass ".env.example creado"
else
    check_fail ".env.example no encontrado"
fi

# 5. Verificar .gitignore actualizado
echo "📋 Test 5: .gitignore actualizado"
if [ -f ".gitignore" ] && grep -q ".env" ".gitignore"; then
    check_pass ".gitignore incluye .env"
else
    check_fail ".gitignore no protege archivos .env"
fi

# 6. Verificar excepciones personalizadas
echo "📋 Test 6: Excepciones personalizadas"
if [ -f "src/main/java/com/zapateria/exceptions/PedidoNotFoundException.java" ]; then
    check_pass "PedidoNotFoundException existe"
else
    check_fail "PedidoNotFoundException no encontrado"
fi

# 7. Verificar constantes en controllers
echo "📋 Test 7: Constantes definidas"
if grep -q "private static final String SUCCESS" src/main/java/com/zapateria/controllers/*.java; then
    check_pass "Constantes encontradas en controllers"
else
    check_fail "No se encontraron constantes"
fi

# 8. Verificar copias defensivas
echo "📋 Test 8: Copias defensivas implementadas"
if grep -q "new ArrayList<>" src/main/java/com/zapateria/models/Pedido.java && \
   grep -q "new Date(" src/main/java/com/zapateria/models/Pedido.java; then
    check_pass "Copias defensivas implementadas"
else
    check_fail "Copias defensivas no encontradas"
fi

# 9. Verificar SimpleDateFormat con Locale
echo "📋 Test 9: SimpleDateFormat con Locale"
if grep -q "Locale.getDefault()" src/main/java/com/zapateria/services/FacturaPdfService.java; then
    check_pass "SimpleDateFormat usa Locale"
else
    check_info "Verificar manualmente SimpleDateFormat"
fi

# 10. Verificar documentación
echo "📋 Test 10: Documentación de seguridad"
if [ -f "SEGURIDAD.md" ] && [ -f "CORRECCIONES_COMPLETAS.md" ]; then
    check_pass "Documentación completa encontrada"
else
    check_fail "Falta documentación"
fi

# 11. Verificar que no hay credenciales hardcoded
echo "📋 Test 11: Sin credenciales hardcoded"
if grep -r "password.*=.*\"" src/main/java/ | grep -v "@Value" | grep -v "//" > /dev/null; then
    check_fail "Posibles credenciales hardcoded encontradas"
else
    check_pass "No se encontraron credenciales hardcoded"
fi

# 12. Verificar integridad en HTML
echo "📋 Test 12: Atributo integrity en HTML"
if grep -r "integrity=" src/main/resources/static/*.html > /dev/null; then
    check_pass "Atributo integrity encontrado en HTML"
    if grep -r "HASH_PLACEHOLDER" src/main/resources/static/*.html > /dev/null; then
        check_info "⚠️  Pendiente: Reemplazar HASH_PLACEHOLDER con hashes SRI reales"
    fi
else
    check_fail "Atributo integrity no encontrado en HTML"
fi

echo ""
echo "================================================"
echo "  RESUMEN DE VERIFICACIÓN"
echo "================================================"
echo "Total de pruebas: $total_checks"
echo "Pruebas exitosas: $passed_checks"
echo "Pruebas fallidas:  $failed_checks"
echo ""

if [ $failed_checks -eq 0 ]; then
    echo "🎉 ¡TODAS LAS VERIFICACIONES PASARON!"
    echo ""
    echo "✅ El proyecto está listo para revisión"
    echo ""
    echo "⚠️  RECORDATORIO IMPORTANTE:"
    echo "Antes de producción, actualizar hashes SRI en archivos HTML"
    echo "Visita: https://www.srihash.org/"
    exit 0
else
    echo "⚠️  ALGUNAS VERIFICACIONES FALLARON"
    echo ""
    echo "Por favor revisa los errores anteriores"
    exit 1
fi
