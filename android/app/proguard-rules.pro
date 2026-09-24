# Release: ofuscação (R8) — regras mínimas.
# Mantém apenas o necessário para stack traces legíveis.

-keepattributes SourceFile,LineNumberTable

# Compose não usa reflexão em runtime; nenhuma regra extra é necessária.
