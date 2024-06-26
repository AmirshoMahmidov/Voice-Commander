package com.example.voicecommander_1

import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.media.MediaPlayer
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieDrawable
import com.example.voicecommander_1.databinding.ActivityMainBinding
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding // Создание переменной для привязки View Binding
    private var isFlashOn = false // Переменная для отслеживания состояния фонарика
    private lateinit var cameraManager: CameraManager // Переменная для управления камерой
    private var cameraId: String? = null // Идентификатор камеры
    private lateinit var mediaPlayer: MediaPlayer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater) // Инициализация View Binding
        setContentView(binding.root) // Установка корневого элемента разметки с использованием View Binding

        // Инициализация CameraManager
        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            cameraId = cameraManager.cameraIdList[0] // Получение идентификатора камеры
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }

        // Установка слушателя для кнопки микрофона
        binding.btnMic.setOnClickListener {
            onClickMic() // Вызов функции для обработки нажатия на кнопку
        }

        // Запуск анимации при создании активности с повторением
        binding.lottieAnimationView.setAnimation("animation.json")
        binding.lottieAnimationView.repeatCount = LottieDrawable.INFINITE // Повторение анимации бесконечно
        binding.lottieAnimationView.playAnimation()

        // Настройка фоновой анимации
        binding.lottieBackgroundAnimationView.setAnimation("background_animation.json")
        binding.lottieBackgroundAnimationView.repeatCount = LottieDrawable.INFINITE
        binding.lottieBackgroundAnimationView.playAnimation()


        mediaPlayer = MediaPlayer.create(this, R.raw.bam)
    }

    // Функция для обработки нажатия на кнопку микрофона
    private fun onClickMic() {
        // Уменьшение громкости музыки до 30% от максимальной
        mediaPlayer.setVolume(0.0f, 0.0f)

        // Создание интента для распознавания речи
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM

        ) // Установка параметров распознавания речи, модель языка свободной формы
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault()) // Установка языка по умолчанию
        startActivityForResult(intent, 10) // Запуск активности распознавания речи с ожиданием результата


    }

    // Обработка результата распознавания речи
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null) { // Проверка успешности получения результата от активности распознавания речи
            when (requestCode) {
                10 -> { // Проверка, что результат получен от нужной активности
                    // Получение распознанного текста из результата
                    val text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    // Установка распознанного текста в TextView с использованием View Binding
                    binding.textTest.text = text!![0]

                    // Отображение соответствующего изображения на основе распознанного текста
                    when {
                        text[0].contains("яблоко", ignoreCase = true) -> {
                            binding.imageView.setImageResource(R.drawable.yab) // Установка изображения яблока
                            binding.imageView.visibility = View.VISIBLE // Отображение ImageView
                        }
                        text[0].contains("абу", ignoreCase = true) -> {
                            binding.imageView.setImageResource(R.drawable.abu) // Установка изображения груши
                            binding.imageView.visibility = View.VISIBLE // Отображение ImageView
                        }
                        text[0].contains("арбуз", ignoreCase = true) -> {
                            binding.imageView.setImageResource(R.drawable.arb) // Установка изображения груши
                            binding.imageView.visibility = View.VISIBLE // Отображение ImageView
                        }
                        text[0].contains("дыня", ignoreCase = true) -> {
                            binding.imageView.setImageResource(R.drawable.din) // Установка изображения дыни
                            binding.imageView.visibility = View.VISIBLE // Отображение ImageView
                        }
                        text[0].contains("свет", ignoreCase = true) -> {
                            toggleFlashlight(true) // Включение фонарика
                        }
                        text[0].contains("куш", ignoreCase = true) -> {
                            toggleFlashlight(false) // Выключение фонарика
                        }
                        text[0].contains("музыка", ignoreCase = true) -> {
                            mediaPlayer.start() // Воспроизведение музыки
                            Toast.makeText(this, "Музыка играет", Toast.LENGTH_SHORT).show()
                        }
                        text[0].contains("стоп", ignoreCase = true) -> {
                            mediaPlayer.pause() // Пауза воспроизведения музыки
                            mediaPlayer.seekTo(0) // Возврат на начало трека
                            Toast.makeText(this, "Музыка остановлена", Toast.LENGTH_SHORT).show()
                        }
                        text[0].contains("пауза", ignoreCase = true) -> {
                            mediaPlayer.pause() // Пауза воспроизведения музыки
                            // Возврат на начало трека
                            Toast.makeText(this, "Музыка остановлена", Toast.LENGTH_SHORT).show()
                        }
                        text[0].contains("выход", ignoreCase = true) -> {
                            finish()

                        }

                        else -> {
                            binding.imageView.visibility = View.INVISIBLE // Скрытие ImageView, если текст не соответствует ни одному условию

                        }
                    }

                    // Восстановление громкости музыки до максимальной после обработки команды
                    mediaPlayer.setVolume(1f, 1f)
                }
            }
        }
    }

    // Функция для включения/выключения фонарика
    private fun toggleFlashlight(turnOn: Boolean) {
        try {
            if (turnOn) {
                cameraManager.setTorchMode(cameraId!!, true) // Включение фонарика
                isFlashOn = true
                Toast.makeText(this, "СВЕТ ГИРОН ШУД", Toast.LENGTH_SHORT).show()
            } else {
                cameraManager.setTorchMode(cameraId!!, false) // Выключение фонарика
                isFlashOn = false
                Toast.makeText(this, "СВЕТ КУШТА ШУД", Toast.LENGTH_SHORT).show()
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }
}