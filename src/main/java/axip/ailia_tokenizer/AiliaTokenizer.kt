package axip.ailia_tokenizer

import android.util.Log

class AiliaTokenizer(
    tokenizerType: Int = AILIA_TOKENIZER_TYPE_XLM_ROBERTA,
) {
    companion object {
        const val AILIA_TOKENIZER_FLAG_NONE = 0

        const val AILIA_TOKENIZER_TYPE_WHISPER = 0;
        const val AILIA_TOKENIZER_TYPE_CLIP = 1;
        const val AILIA_TOKENIZER_TYPE_XLM_ROBERTA = 2;
        const val AILIA_TOKENIZER_TYPE_MARIAN = 3;
        const val AILIA_TOKENIZER_TYPE_BERT_JAPANESE_WORDPIECE = 4;
        const val AILIA_TOKENIZER_TYPE_BERT_JAPANESE_CHARACTER = 5;
        const val AILIA_TOKENIZER_TYPE_T5 = 6;
        const val AILIA_TOKENIZER_TYPE_ROBERTA = 7;

        init {
            System.loadLibrary("ailia_tokenizer")
        }
    }

    private val tag = AiliaTokenizer::class.simpleName
    private var tokenizer: Long = 0

    init {
        tokenizer = create(tokenizerType, AILIA_TOKENIZER_FLAG_NONE)
    }

    fun loadFiles(modelPath: String? = null, vocabPath: String? = null, mergePath: String? = null,
                  dictionaryPath: String? = null) {
        modelPath?.let {
            val result = openModelFile(tokenizer, modelPath)
            if (result != 0) {
                Log.d(tag, "Failed to load model file: $result")
            }
        }

        vocabPath?.let {
            val result = openVocabFile(tokenizer, vocabPath)
            if (result != 0) {
                Log.d(tag,"Failed to load vocab file: $result")
            }
        }

        mergePath?.let {
            val result = openMergeFile(tokenizer, mergePath)
            if (result != 0) {
                Log.d(tag,"Failed to load merge file: $result")
            }
        }

        dictionaryPath?.let {
            val result = openDictionaryFile(tokenizer, dictionaryPath)
            if (result != 0) {
                Log.d(tag,"Failed to load dictionary file: $result")
            }
        }
    }

    fun encode(text: String): IntArray {
        // First encode the text
        val encodeResult = encode(tokenizer, text)
        if (encodeResult != 0) {
            Log.d(tag,"Failed to encode text: $encodeResult")
        }

        // Get token count
        val tokenCount = getTokenCount(tokenizer)

        // Get tokens
        val tokens = IntArray(tokenCount)
        val tokenResult = getTokens(tokenizer, tokens, tokenCount)
        if (tokenResult != 0) {
            Log.d(tag,"Failed to get tokens: $tokenResult")
        }

        return tokens
    }

    fun getWord(token: Int): String {
        return getVocab(tokenizer, token)
    }

    fun close() {
        destroy(tokenizer)
    }

    private external fun create(tokenizerType: Int, flags: Int): Long
    private external fun destroy(tokenizer: Long): Void
    private external fun openModelFile(tokenizer: Long, path: String): Int
    private external fun openVocabFile(tokenizer: Long, path: String): Int
    private external fun openMergeFile(tokenizer: Long, path: String): Int
    private external fun openDictionaryFile(tokenizer: Long, path: String): Int
    private external fun encode(tokenizer: Long, text: String): Int
    private external fun getTokenCount(tokenizer: Long): Int
    private external fun getTokens(tokenizer: Long, tokens: IntArray, count: Int): Int
    private external fun getVocab(tokenizer: Long, token: Int): String
}