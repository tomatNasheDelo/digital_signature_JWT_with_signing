# Digital signatures and signed JWTs with Java/Spring Boot

Учебный проект показывает связь двух уровней:

1. Цифровая подпись напрямую через Java Cryptography Architecture (JCA), алгоритм `SHA256withRSA`.
2. Подписанный JWT, алгоритм `RS256` (RSA + SHA-256), создаваемый и проверяемый библиотекой JJWT.

## Требования

- JDK 17+
- Gradle 8+
- OpenSSL для генерации RSA-ключей

## Быстрый старт

Сгенерируйте пару ключей RSA (2048 бит или больше):

```bash
openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:3072 -out private.pem
openssl pkey -in private.pem -pubout -out public.pem
```

Передайте ключи приложению через переменные окружения и запустите:

```bash
export RSA_PUBLIC_KEY_PEM="$(cat public.pem)"
export RSA_PRIVATE_KEY_PEM="$(cat private.pem)"
gradle bootRun
```

PowerShell:

```powershell
$env:RSA_PUBLIC_KEY_PEM = Get-Content -Raw public.pem
$env:RSA_PRIVATE_KEY_PEM = Get-Content -Raw private.pem
gradle bootRun
```

Не коммитьте приватный ключ или PEM-файлы. В реальной системе используйте secret manager/KMS и план ротации ключей.

## REST API

### Подписать произвольный текст

```bash
curl -sS -X POST http://localhost:8080/api/signature/sign \
  -H 'Content-Type: application/json' \
  -d '{"payload":"hello from JCA signature"}'
```

Ответ содержит `signatureBase64`. Проверка подписи:

```bash
curl -sS -X POST http://localhost:8080/api/signature/verify \
  -H 'Content-Type: application/json' \
  -d '{"payload":"hello from JCA signature","signatureBase64":"<signature-from-response>"}'
```

Измените payload и повторите проверку: ответ будет `{"valid":false}`.

### Создать JWT с RS256

```bash
curl -sS -X POST http://localhost:8080/api/jwt/create \
  -H 'Content-Type: application/json' \
  -d '{"issuer":"demo-issuer","subject":"alice","role":"reader","ttlSeconds":300}'
```

Проверить токен и извлечь claims:

```bash
curl -sS -X POST http://localhost:8080/api/jwt/validate \
  -H 'Content-Type: application/json' \
  -d '{"token":"<jwt-from-response>"}'
```

Проверка подписи и срока действия выполняется при разборе токена. В production также задайте и проверяйте ожидаемые `issuer` и `audience`, применяйте короткий срок действия и собственные правила авторизации.

## Запуск тестов

```bash
gradle test
```

Тесты проверяют создание и проверку подписи, изменение payload, выпуск/валидацию JWT и REST-вызовы через MockMvc. Интеграционные тесты используют временную RSA-пару; production ключи не нужны.

## Важные понятия

- Подпись обеспечивает целостность и подтверждает, что токен подписан обладателем приватного ключа; она не шифрует сообщение.
- JWT в формате JWS — это Base64URL-кодированные header и payload плюс подпись. Любой, у кого есть токен, может декодировать его header и payload. Не помещайте туда пароли и секреты.
- Подписывают приватным ключом, проверяют публичным. Публичный ключ сам по себе не доказывает личность владельца — для этого важны доверенная доставка ключа и, позднее, сертификаты/PKI.
- Проверяйте только ожидаемые алгоритмы, claims и срок действия. Не используйте JWT как единственный механизм авторизации без проверки контекста и прав.

X.509-сертификаты и PKI намеренно оставлены для следующего этапа изучения.
