package com.example.speechtranslation.AuthenticationTokens

class TokenAuthentication {
    val TRANSLATE_API_URL = "https://translation.googleapis.com/language/translate/v2?"
    val TEXT_TO_SPEECH_URL = "https://texttospeech.googleapis.com/v1/text:synthesize?"
    val KEY = "key=AIzaSyACauevY5zkZmc6WKt2iC5_QzRRAJ8Wzo4"
    val TRANSLATE_GOOGLE_API = "https://translate.googleapis.com/\$discovery/rest?version=v3"

    fun getFinalTextToSpeechUrl() : String {
        return TEXT_TO_SPEECH_URL + KEY
    }

    fun getSuffixTranslateApiURl() : String {
        return TRANSLATE_API_URL + KEY
    }
}