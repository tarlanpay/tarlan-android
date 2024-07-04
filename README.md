# Документация по использованию Tarlan SDK

## Запуск Tarlan SDK с использованием `activityLauncher`

### Описание

Для интеграции Tarlan SDK в ваш Android проект необходимо использовать `activityLauncher` для запуска Tarlan SDK и обработки его результата. 

### Шаги интеграции

1. **Регистрация ActivityResultLauncher**: Зарегистрируйте `activityLauncher` с использованием `TarlanContract`.
2. **Передача параметров**: Запустите `activityLauncher` с передачей необходимых параметров через `TarlanInput`.
3. **Обработка результата**: Обработайте результат выполнения SDK, используя предоставленные `TarlanOutput` классы.

### Регистрация `activityLauncher`

Зарегистрируйте `activityLauncher` в вашей `Activity` или `Fragment` с использованием `TarlanContract` для получения результата от Tarlan SDK.

```kotlin
val activityLauncher = registerForActivityResult(TarlanContract()) { result ->
    when (result) {
        is TarlanOutput.Success -> {
            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
        }
        is TarlanOutput.Failure -> {
            Toast.makeText(this, "Failure", Toast.LENGTH_SHORT).show()
        }
    }
}
```
Запуск Tarlan SDK
Для запуска Tarlan SDK, используйте activityLauncher и передайте необходимые параметры через TarlanInput.

Параметры для запуска
1. **hash** (String): Хэш транзакции.
2. **transactionId** (Long): Идентификатор транзакции.
3. **isDebug** (Boolean): Флаг, указывающий на режим отладки.
Пример вызова:

```kotlin
activityLauncher.launch(
    TarlanInput(
        hash = "your_hash_value",
        transactionId = 123456789L,
        isDebug = false
    )
)
```
Обработка результата
Результат работы Tarlan SDK возвращается через activityLauncher и может быть одного из двух типов:

TarlanOutput.Success: Успешное выполнение операции.
TarlanOutput.Failure: Ошибка выполнения операции.
Пример обработки результата:

```kotlin
val activityLauncher = registerForActivityResult(TarlanContract()) { result ->
    when (result) {
        is TarlanOutput.Success -> {
            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
        }
        is TarlanOutput.Failure -> {
            Toast.makeText(this, "Failure", Toast.LENGTH_SHORT).show()
        }
    }
}
```
