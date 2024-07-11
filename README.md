
[<img width="250" height="119" src="https://github.com/tarlanpay/tarlan-android/blob/main/res/logo.svg"/>](https://docs.tarlanpayments.kz)

# Tarlan Pay Android SDK

[![GitHub release](https://img.shields.io/github/release/tarlanpay/tarlan-android.svg?maxAge=60)](https://github.com/tarlanpay/tarlan-android/releases)

SDK Tarlan Pay для Android позволяет быстро и легко интегрировать платежный функционал в ваше Android приложение. Мы предоставляем мощные и настраиваемые UI-элементы, которые можно использовать "из коробки" для сбора платежных данных ваших пользователей. 

## Быстрый старт

Для начала работы с SDK Tarlan Pay следуйте этим простым шагам:

1. **Добавьте зависимости**: Добавьте SDK в ваш `build.gradle` файл.
2. **Запуск Tarlan SDK**: Используйте `activityLauncher` для запуска Tarlan SDK и обработки его результата.
3. **Обработка результатов**: Используйте `TarlanOutput` для обработки результата.

### Шаг 1: Добавьте зависимости

```groovy
dependencies {
    implementation 'kz.tarlanpayments.storage:androidsdk:1.0.6'
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

## Поддержка

Если у вас возникли вопросы или проблемы, пожалуйста, посетите наш [центр поддержки](https://tarlanpayments.kz/faq).
