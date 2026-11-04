# StepKol — Лабораторная работа №4

## Вариант
**19 3 2** (схема V.N.T)

| Компонент | Значение | Описание |
|-----------|----------|----------|
| **V = 19** | Предметная область | Шагометр + счётчик калорий (из лабораторной работы №3) |
| **N = 3** | Навигация | **Menu** — выпадающее навигационное меню (Options Menu) для переходов между окнами |
| **T = 2** | Передача данных | **T2** — передача данных **из других окон в начальное** (MainActivity) |

---

## 1. Общая архитектура

Приложение состоит из трёх активностей (окон):

```
┌─────────────┐      меню       ┌──────────────┐
│  Главная    │ ◄──────────────► │   Профиль    │
│ (MainActivity)               │ (ProfileActivity)
│                             │ (имя, вес, рост)
└─────────────┘                └──────┬────────┘
      │                               │
      │ меню (передача итогов)        │ меню
      ▼                               ▼
┌─────────────┐                      │
│Статистика   │ ◄────────────────────┘
│(StatisticsActivity)
│(итоги шагов, ккал)
└─────────────┘
```

- **Главное окно** — ввод количества шагов, расчёт дистанции и калорий, просмотр истории.
- **Профиль** — ввод имени, веса и роста. Данные сохраняются и передаются обратно в главное окно.
- **Статистика** — отображение суммарной статистики по всем записям.

---

## 2. Навигационное меню (N3 – Menu)

### Файл: `app/src/main/res/menu/main_menu.xml`

```xml
<menu xmlns:android="http://schemas.android.com/apk/res/android">
    <item
        android:id="@+id/menu_main"
        android:title="@string/menu_main" />
    <item
        android:id="@+id/menu_profile"
        android:title="@string/menu_profile" />
    <item
        android:id="@+id/menu_statistics"
        android:title="@string/menu_statistics" />
</menu>
```

- `menu_main` — «Главная», переход на главный экран.
- `menu_profile` — «Профиль», открытие окна профиля.
- `menu_statistics` — «Статистика», открытие окна статистики.

### Почему меню появилось без Toolbar

В файле `app/src/main/res/values/themes.xml` тема приложения изменена:

```xml
<style name="Base.Theme.Stepkol" parent="Theme.AppCompat.DayNight.DarkActionBar">
```

`DarkActionBar` предоставляет системную панель действий (ActionBar) в верхней части каждого окна. Danki этому, методы `onCreateOptionsMenu()` и `onOptionsItemSelected()` автоматически работают во всех активностях — не нужно вручную добавлять `Toolbar` в каждый layout.

### Подключение меню в активности (пример MainActivity)

```kotlin
override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.main_menu, menu)
    return true
}

override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
        R.id.menu_main -> {
            // Уже на главном экране — ничего не делаем
            true
        }
        R.id.menu_profile -> {
            openProfile()
            true
        }
        R.id.menu_statistics -> {
            openStatistics()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
```

- `onCreateOptionsMenu` — надувает XML-меню в панель действий.
- `onOptionsItemSelected` — обрабатывает нажатие на пункт меню и запускает соответствующую активность через `Intent`.

---

## 3. Передача данных из главного окна в другие (T1)

### Главная → Статистика

В `MainActivity.kt` метод `openStatistics()` создаёт `Intent`, кладёт в него итоговые значения и запускает активность:

```kotlin
private fun openStatistics() {
    val intent = Intent(this, StatisticsActivity::class.java)
    intent.putExtra(StatisticsActivity.EXTRA_USER_NAME, userName)
    intent.putExtra(StatisticsActivity.EXTRA_TOTAL_STEPS, StepCalculator.totalSteps(history))
    intent.putExtra(StatisticsActivity.EXTRA_TOTAL_CALORIES, StepCalculator.totalCalories(history))
    startActivity(intent)
}
```

Ключи передачи определены в `StatisticsActivity` как константы:

```kotlin
companion object {
    const val EXTRA_USER_NAME = "com.example.stepkol.EXTRA_USER_NAME"
    const val EXTRA_TOTAL_STEPS = "com.example.stepkol.EXTRA_TOTAL_STEPS"
    const val EXTRA_TOTAL_CALORIES = "com.example.stepkol.EXTRA_TOTAL_CALORIES"
}
```

### Главная → Профиль

В `MainActivity.kt` метод `openProfile()` передаёт текущие имя, вес и рост в окно профиля, чтобы поля были предзаполнены:

```kotlin
private fun openProfile() {
    val intent = Intent(this, ProfileActivity::class.java)
    intent.putExtra(ProfileActivity.EXTRA_NAME, userName)
    intent.putExtra(ProfileActivity.EXTRA_WEIGHT, userWeight)
    intent.putExtra(ProfileActivity.EXTRA_HEIGHT, userHeight)
    profileLauncher.launch(intent)
}
```

---

## 4. Передача данных из других окон в главное (T2) — основное требование варианта

### Профиль → Главное

Это ключевая часть задания **T2** — данные отправляются **обратно** в начальное окно через механизм результата активности (`setResult`).

#### Как это работает:

1. В `MainActivity` создан `ActivityResultLauncher`, который ожидает результат от `ProfileActivity`:

```kotlin
private val profileLauncher = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult()
) { result ->
    if (result.resultCode == Activity.RESULT_OK) {
        val data = result.data ?: return@registerForActivityResult
        userName = data.getStringExtra(ProfileActivity.EXTRA_NAME) ?: ""
        userWeight = data.getDoubleExtra(ProfileActivity.EXTRA_WEIGHT, 0.0)
        userHeight = data.getIntExtra(ProfileActivity.EXTRA_HEIGHT, 0)
        updateUserDisplay()
    }
}
```

2. В `ProfileActivity.kt` при нажатии «Сохранить» данные упаковываются в `Intent` и отправляются обратно:

```kotlin
// Отправляем данные обратно в главное окно (T2)
val result = Intent()
result.putExtra(EXTRA_NAME, name)
result.putExtra(EXTRA_WEIGHT, weight)
result.putExtra(EXTRA_HEIGHT, height)
setResult(Activity.RESULT_OK, result)
Toast.makeText(this, R.string.msg_saved, Toast.LENGTH_SHORT).show()
finish()
```

- `setResult(Activity.RESULT_OK, result)` — устанавливает код успеха и прикрепляет данные.
- `finish()` — закрывает окно профиля, управление возвращается в `MainActivity`, где срабатывает `profileLauncher` и забирает данные.

### Ключи передачи в ProfileActivity

```kotlin
companion object {
    const val EXTRA_NAME = "com.example.stepkol.EXTRA_NAME"
    const val EXTRA_WEIGHT = "com.example.stepkol.EXTRA_WEIGHT"
    const val EXTRA_HEIGHT = "com.example.stepkol.EXTRA_HEIGHT"
}
```

---

## 5. Логика главного экрана после изменений

### Файл: `MainActivity.kt`

**Что убрано:**
- Поля ввода веса (`etWeight`) и роста (`etHeight`) — они теперь берутся только из профиля.

**Что осталось:**
- Поле «Количество шагов» (`etSteps`).
- Кнопка «Рассчитать и добавить».
- История расчётов (`lvHistory`).
- Общая статистика (`tvTotal`).
- Отображение имени пользователя (`tvUser`).

**Как работает расчёт:**

```kotlin
btnAdd.setOnClickListener {
    val steps = etSteps.text.toString().toIntOrNull()
    if (steps == null || steps <= 0) {
        etSteps.error = "Введите число шагов"
        return@setOnClickListener
    }

    // Вес и рост берутся из профиля
    if (userWeight <= 0) {
        Toast.makeText(this, "Сначала укажите вес в профиле", Toast.LENGTH_SHORT).show()
        return@setOnClickListener
    }
    if (userHeight <= 0) {
        Toast.makeText(this, "Сначала укажите рост в профиле", Toast.LENGTH_SHORT).show()
        return@setOnClickListener
    }

    val entry = StepCalculator.calculate(steps, userWeight, userHeight)
    tvResult.text = "Дистанция: %.2f км\nКалории: %.1f ккал"
        .format(entry.distanceKm, entry.calories)

    history.add(0, entry)
    adapter.notifyDataSetChanged()
    updateTotal(tvTotal)
    etSteps.text.clear()
}
```

1. Проверяется, введено ли положительное число шагов.
2. Проверяется, заполнен ли профиль (вес и рост > 0). Если нет — показывается `Toast` с просьбой зайти в профиль.
3. Вызывается `StepCalculator.calculate()` — считает дистанцию и калории по формулам из 3-й лабораторной.
4. Результат добавляется в начало истории, обновляется общая статистика.
5. Поле шагов очищается.

### Отображение пользователя

```kotlin
private fun updateUserDisplay() {
    tvUser.text = if (userName.isNotEmpty()) {
        getString(R.string.stat_user, userName)
    } else {
        "Пользователь: не указан"
    }
}
```

Вызывается при запуске экрана и при возврате из профиля — показывает имя или заглушку.

---

## 6. Окно профиля (`ProfileActivity`)

### Файл: `ProfileActivity.kt`

**Экраны и поля:**
- `etName` — имя пользователя (`textPersonName`).
- `etWeight` — вес в кг (`numberDecimal`).
- `etHeight` — рост в см (`number`).
- `btnSave` — кнопка «Сохранить».

**Поток работы:**

1. При открытии (`onCreate`) из `Intent` достаются текущие значения имени/веса/роста (переданные из главного окна) и подставляются в поля для редактирования.
2. При нажатии «Сохранить» происходит валидация:
   - Имя не пустое.
   - Вес — число > 0.
   - Рост — число > 0.
3. Если валидация пройдена — данные отправляются обратно в `MainActivity` через `setResult()` (T2), показывается `Toast` «Данные сохранены», окно закрывается (`finish()`).
4. Если валидация не пройдена — соответствующему полю устанавливается ошибка (`setError`).

---

## 7. Окно статистики (`StatisticsActivity`)

### Файл: `StatisticsActivity.kt`

**Что отображается:**
- `tvUser` — имя пользователя.
- `tvTotalSteps` — всего шагов.
- `tvTotalCalories` — всего калорий.

**Как получает данные:**

```kotlin
val name = intent.getStringExtra(EXTRA_USER_NAME) ?: ""
val steps = intent.getIntExtra(EXTRA_TOTAL_STEPS, 0)
val calories = intent.getDoubleExtra(EXTRA_TOTAL_CALORIES, 0.0)

tvUser.text = getString(R.string.stat_user, name)
tvTotalSteps.text = getString(R.string.stat_total_steps, steps)
tvTotalCalories.text = getString(R.string.stat_total_calories, calories)
```

Данные приходят из главного окна через `Intent extras` (T1 — из начального в другое).

---

## 8. Регистрация активностей в манифесте

### Файл: `app/src/main/AndroidManifest.xml`

Все три активности зарегистрированы:

```xml
<activity
    android:name=".MainActivity"
    android:exported="true"
    android:windowSoftInputMode="adjustResize">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>
<activity
    android:name=".ProfileActivity"
    android:exported="false"
    android:windowSoftInputMode="adjustResize" />
<activity
    android:name=".StatisticsActivity"
    android:exported="false"
    android:windowSoftInputMode="adjustResize" />
```

- `MainActivity` — точка входа (`MAIN` + `LAUNCHER`), `exported="true"`.
- `ProfileActivity` и `StatisticsActivity` — внутренние окна, `exported="false"` (не доступны извне приложения).
- `windowSoftInputMode="adjustResize"` — экран сдвигается при открытии клавиатуры, чтобы поля ввода не закрывались.

---

## 9. Итоговая схема передачи данных (T1 + T2)

```
┌─────────────────────────────────────────────────────────────────┐
│                     ГЛАВНОЕ ОКНО (MainActivity)                 │
│  userName, userWeight, userHeight                               │
│  history (список StepEntry)                                     │
└─────────────────────────────────────────────────────────────────┘
          │                                          │
          │ putExtra (имя, вес, рост)                │ putExtra (итоги шагов, ккал)
          ▼                                          ▼
┌─────────────────────┐                    ┌─────────────────────┐
│   ОКНО ПРОФИЛЯ      │                    │   ОКНО СТАТИСТИКИ   │
│  (ProfileActivity)  │                    │(StatisticsActivity) │
│                     │                    │                     │
│  setResult(RESULT_OK)│                    │  (только получение) │
│  ◄──────────────────┘                    └─────────────────────┘
│      (T2: данные возвращаются в главное)
└─────────────────────────────────────────────────────────────────┘
```

---

## 10. Как пользоваться приложением

1. **Запуск** — открывается главный экран шагомера.
2. **Первый вход** — в меню (три точки) выберите **Профиль**.
3. Введите **имя, вес и рост**, нажмите «Сохранить». Данные вернутся на главный экран.
4. На главном экране введите только **количество шагов** и нажмите «Рассчитать и добавить».
   - Если профиль не заполнен — появится напоминание.
   - Результат добавится в историю, общая статистика обновится.
5. Через меню можно перейти в **Статистику** и посмотреть итоги.
6. Из любого окна через пункт **Главная** можно вернуться на главный экран.

---

## 11. Файлы проекта

### Созданные
| Путь | Назначение |
|------|------------|
| `app/src/main/res/menu/main_menu.xml` | Навигационное меню (3 пункта) |
| `app/src/main/java/com/example/stepkol/ProfileActivity.kt` | Активность профиля |
| `app/src/main/java/com/example/stepkol/StatisticsActivity.kt` | Активность статистики |
| `app/src/main/res/layout/activity_profile.xml` | Layout профиля |
| `app/src/main/res/layout/activity_statistics.xml` | Layout статистики |
| `README.md` | Данная документация |

### Изменённые
| Путь | Что изменено |
|------|--------------|
| `app/src/main/java/com/example/stepkol/MainActivity.kt` | Добавлено меню, переходы, приём результата (T2); убраны поля веса/роста |
| `app/src/main/res/layout/activity_main.xml` | Убраны `etWeight`, `etHeight`; добавлен `tvUser` |
| `app/src/main/res/values/strings.xml` | Добавлены строки для меню, профиля, статистики |
| `app/src/main/res/values/themes.xml` | Тема → `Theme.AppCompat.DayNight.DarkActionBar` |
| `app/src/main/AndroidManifest.xml` | Зарегистрированы `ProfileActivity`, `StatisticsActivity` |

---

## 12. Требования

- **minSdk**: 24
- **Язык**: Kotlin
- **Сборка**: Gradle, Android Studio
- **Зависимости**: `androidx.appcompat`, `androidx.activity`, `androidx.constraintlayout`, `com.google.android.material`