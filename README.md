
[<img width="250" height="119" src="https://github.com/tarlanpay/tarlan-android/blob/main/res/logo.svg"/>](https://docs.tarlanpayments.kz)

# Tarlan Pay Android SDK

[![GitHub release](https://img.shields.io/github/v/release/tarlanpay/tarlan-android.svg?maxAge=60)](https://github.com/tarlanpay/tarlan-android/releases)

SDK Tarlan Pay для Android позволяет быстро и легко интегрировать платежный функционал в ваше Android приложение. Мы предоставляем мощные и настраиваемые UI-элементы, которые можно использовать "из коробки" для сбора платежных данных ваших пользователей. 

## Быстрый старт

Для начала работы с SDK Tarlan Pay следуйте этим простым шагам:

1. **Добавьте зависимости**: Добавьте SDK в ваш `build.gradle` файл.
2. **Запуск Tarlan SDK**: Используйте `activityLauncher` для запуска Tarlan SDK и обработки его результата.
3. **Обработка результатов**: Используйте `TarlanOutput` для обработки результата.

### Шаг 1: Добавьте зависимости

```groovy
dependencies {
    implementation 'kz.tarlanpayments.storage:androidsdk:1.1.2'
}
```

### Шаг 2: Запуск Tarlan SDK

Используйте `activityLauncher` для запуска Tarlan SDK и обработки его результата.

#### Регистрация `activityLauncher`

Зарегистрируйте `activityLauncher` в вашей `Activity` или `Fragment` с использованием `TarlanContract` для получения результата от Tarlan SDK.

```kotlin
val activityLauncher = registerForActivityResult(TarlanContract()) { result ->
    when (result) {
        is TarlanOutput.Success -> {
            Toast.makeText(this, "Успех", Toast.LENGTH_SHORT).show()
        }
        is TarlanOutput.Failure -> {
            Toast.makeText(this, "Ошибка", Toast.LENGTH_SHORT).show()
        }
    }
}
```

#### Запуск Tarlan SDK

Для запуска Tarlan SDK, используйте `activityLauncher` и передайте необходимые параметры через `TarlanInput`.

```kotlin
activityLauncher.launch(
    TarlanInput(
        hash = "your_hash_value",
        transactionId = 123456789L,
        isDebug = false
    )
)
```

### Шаг 3: Обработка результатов

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

## Google Pay
Узнайте, как принимать платежи с помощью Google Pay.

### Условия использования Google Pay
Интегрируя Google Pay, вы соглашаетесь с условиями обслуживания Google.

Google Pay позволяет клиентам совершать платежи в вашем приложении с помощью любой кредитной или дебетовой карты, сохраненной в их учетной записи Google, включая карты из Google Play, YouTube, Chrome или Android-устройства. Используйте API Google Pay, чтобы запросить любую кредитную или дебетовую карту, сохраненную в учетной записи вашего клиента.

Google Pay полностью совместим с продуктами и функциями Tarlan Pay, позволяя вам использовать его вместо традиционной платежной формы, когда это возможно. 
Для получения дополнительной информации о том, какие покупки должны использовать систему выставления счетов Google Play, см. [условия для разработчиков Google Play](https://developer.android.com/google/play/billing)

### Примите платеж с помощью Google Pay в вашем Android-приложении

#### Требования
Чтобы поддерживать Google Pay на Android, вам необходимо следующее:

- minSdkVersion 19 или выше.
- compileSdkVersion 28 или выше.

Кроме того, если вы хотите протестировать на своем устройстве, вам нужно добавить способ оплаты в свою учетную запись Google. См. [добавление способа оплаты](https://support.google.com/wallet/answer/12058983?visit_id=637947092743186187-653786796&rd=1).

#### Настройка интеграции
Чтобы использовать Google Pay, сначала включите API Google Pay, добавив следующее в тег `<application>` вашего `AndroidManifest.xml`:

```xml
<application>
  ...
  <meta-data
    android:name="com.google.android.gms.wallet.api.enabled"
    android:value="true" />
</application>
```

Добавьте в консоле возможность оплаты через `Google pay` и в форме появиться кнопка опталы.

Для получения дополнительной информации см. [настройку API Google Pay для Android](https://developers.google.com/pay/api/android).


#### Использование Sdk without UI

## Быстрый старт

Для начала работы с SDK Android SdkNoUI следуйте этим простым шагам:
- **Шифрование данных:** SDK самостоятельно шифрует все чувствительные данные, такие как номера карт и CVV-коды, обеспечивая безопасность транзакций.
- **Обработка результатов:** SDK автоматически обрабатывает результаты транзакций, предоставляя разработчику уже готовые модели данных о состоянии транзакции.

1. **Добавьте зависимости**: Добавьте SDK в ваш `build.gradle` файл.
2. **Инициализация**.
3. **Работа с sdk**.

### Шаг 1: Добавьте зависимости

```groovy
dependencies {
    implementation 'kz.tarlanpayments.storage:androidsdk.noui:1.1.2'
}
```

### Шаг 2: Инициализация Tarlan SDK
```kotlin
    TarlanInstance.init(isDebug = false)
```
### Использование 3: Методы

#### `suspend fun inRq(...) : TarlanTransactionStateModel`

Списание средств с карты.

- **Параметры:**
  - `transactionId` (Long): Уникальный идентификатор транзакции.
  - `hash` (String): Контрольная сумма для валидации.
  - `cardNumber` (String): Номер карты.
  - `cvv` (String): Код безопасности карты.
  - `month` (String): Месяц окончания срока действия карты.
  - `year` (String): Год окончания срока действия карты.
  - `cardHolder` (String): Имя владельца карты.
  - `email` (String?, по умолчанию `null`): Электронная почта.
  - `phone` (String?, по умолчанию `null`): Телефон.
  - `saveCard` (Boolean, по умолчанию `false`): Флаг для сохранения карты.

#### `suspend fun cardLink(...) : TarlanTransactionStateModel`

Привязка карты к транзакции.

- **Параметры:**
  - `transactionId` (Long): Уникальный идентификатор транзакции.
  - `hash` (String): Контрольная сумма для валидации.
  - `cardNumber` (String): Номер карты.
  - `cvv` (String): Код безопасности карты.
  - `month` (String): Месяц окончания срока действия карты.
  - `year` (String): Год окончания срока действия карты.
  - `cardHolder` (String): Имя владельца карты.
  - `email` (String?, по умолчанию `null`): Электронная почта.
  - `phone` (String?, по умолчанию `null`): Телефон.

#### `suspend fun outRq(...) : TarlanTransactionStateModel`

Вывод средств на карту.

- **Параметры:**
  - `transactionId` (Long): Уникальный идентификатор транзакции.
  - `hash` (String): Контрольная сумма для валидации.
  - `cardNumber` (String): Номер карты.
  - `email` (String?, по умолчанию `null`): Электронная почта.
  - `phone` (String?, по умолчанию `null`): Телефон.

#### `suspend fun inFromSavedRq(...) : TarlanTransactionStateModel`

Списание средств с сохраненной карты.

- **Параметры:**
  - `transactionId` (Long): Уникальный идентификатор транзакции.
  - `hash` (String): Контрольная сумма для валидации.
  - `encryptedId` (String): Зашифрованный идентификатор карты.
  - `email` (String?, по умолчанию `null`): Электронная почта.
  - `phone` (String?, по умолчанию `null`): Телефон.

#### `suspend fun outFromSaved(...) : TarlanTransactionStateModel`

Вывод средств на сохраненную карту.

- **Параметры:**
  - `transactionId` (Long): Уникальный идентификатор транзакции.
  - `hash` (String): Контрольная сумма для валидации.
  - `encryptedId` (String): Зашифрованный идентификатор карты.
  - `email` (String?, по умолчанию `null`): Электронная почта.
  - `phone` (String?, по умолчанию `null`): Телефон.

#### `suspend fun googlePay(...) : TarlanTransactionStateModel`

Обработка платежа с помощью Google Pay.

- **Параметры:**
  - `transactionId` (Long): Уникальный идентификатор транзакции.
  - `hash` (String): Контрольная сумма для валидации.
  - `paymentMethodData` (Map<String, Any>): Данные о методе оплаты.

#### `suspend fun getTransactionStatus(...) : TarlanTransactionStatusModel`

Получение статуса транзакции.

- **Параметры:**
  - `transactionId` (Long): Уникальный идентификатор транзакции.
  - `hash` (String): Контрольная сумма для валидации.

#### `suspend fun resumeTransaction(...) : TarlanTransactionStateModel`

Возобновление транзакции.

- **Параметры:**
  - `transactionId` (Long): Уникальный идентификатор транзакции.
  - `hash` (String): Контрольная сумма для валидации.

#### `suspend fun deleteCard(...)`

Удаление сохраненной карты.

- **Параметры:**
  - `transactionId` (Long): Уникальный идентификатор транзакции.
  - `transactionHash` (String): Контрольная сумма транзакции.
  - `projectId` (Long): Уникальный идентификатор проекта.
  - `encryptedCardId` (String): Зашифрованный идентификатор карты.

#### `suspend fun getTransactionDescription(...) : TarlanTransactionDescriptionModel`

Получение описания транзакции.

- **Параметры:**
  - `transactionId` (Long): Уникальный идентификатор транзакции.
  - `hash` (String): Контрольная сумма для валидации.

## Sealed Interface: TarlanTransactionStateModel

### Реализации

#### `data class Success(...) : TarlanTransactionStateModel`

Успех транзакции.

- **Поля:**
  - `transactionId` (Long): Уникальный идентификатор транзакции.
  - `transactionHash` (String): Контрольная сумма транзакции.

#### `data class Waiting3DS(...) : TarlanTransactionStateModel`

Ожидание прохождения 3DS аутентификации.

- **Поля:**
  - `termUrl` (String): URL для завершения 3DS аутентификации.
  - `action` (String): Действие для выполнения.
  - `params` (Map<String, String>): Параметры для 3DS аутентификации.
  - `transactionId` (Long): Уникальный идентификатор транзакции.
  - `transactionHash` (String): Контрольная сумма транзакции.

> Для прохождения 3DS аутентификации, нужно открыть `Tarlan3DSContract`. После прохождения `Tarlan3DSContract`, необходимо вызвать метод `getTransactionStatus` для получения обновленного состояния транзакции.

#### `data class FingerPrint(...) : TarlanTransactionStateModel`

Необходимость прохождения Fingerprint аутентификации.

- **Поля:**
  - `methodData` (String): Данные метода.
  - `methodUrl` (String): URL для метода аутентификации.
  - `transactionId` (Long): Уникальный идентификатор транзакции.
  - `transactionHash` (String): Контрольная сумма транзакции.

> Для прохождения Fingerprint аутентификации, нужно открыть `Tarlan3DSV2Contract`. После этого, вызвать метод `resumeTransaction` и получить новый `TarlanTransactionStateModel` для дальнейших действий в зависимости от статуса.

#### `data class Error(...) : TarlanTransactionStateModel`

Ошибка транзакции.

- **Поля:**
  - `transactionId` (Long): Уникальный идентификатор транзакции.
  - `transactionHash` (String): Контрольная сумма транзакции.
  - `message` (Exception): Сообщение об ошибке.

> В случае ошибки, необходимо создать новую транзакцию.

### Использование setFragmentResultListener

Для вызова `Tarlan3DSContract` и `Tarlan3DSV2Input`, необходимо использовать `registerForActivityResult`.
## Поддержка

Если у вас возникли вопросы или проблемы, пожалуйста, посетите наш [центр поддержки](https://tarlanpayments.kz/faq).
