package com.example.extension.worker

import com.example.extension.objects.trimText
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

fun String.isLeastOnce(s: String): Boolean {
    if (this.isNullOrEmpty()) return false
    return this.matches(""".*[$s].*""".toRegex())
}

fun String.isOnlyAllow(s: String): Boolean {
    if (this.isNullOrEmpty()) return false
    return this.matches("""[$s]+""".toRegex())
}

val VN_ONLY_CHARS get() = "aAàÀảẢãÃáÁạẠăĂằẰẳẲẵẴắẮặẶâÂầẦẩẨẫẪấẤậẬbBcCdDđĐeEèÈẻẺẽẼéÉẹẸêÊềỀểỂễỄếẾệỆfFgGhHiIìÌỉỈĩĨíÍịỊjJkKlLmMnNoOòÒỏỎõÕóÓọỌôÔồỒổỔỗỖốỐộỘơƠờỜởỞỡỠớỚợỢpPqQrRsStTuUùÙủỦũŨúÚụỤưƯừỪửỬữỮứỨựỰvVwWxXyYỳỲỷỶỹỸýÝỵỴzZ "

val NUMBER_CHARS get() = "0-9"

val VN_FULL_CHARS get() = "$NUMBER_CHARS$VN_ONLY_CHARS"

val ADDRESS_CHARS get() = ",.'\\-#@%&/_$VN_FULL_CHARS"

val ID_CHARS get() = "0-9a-zA-Z "

val SPACE_CHAR get() = " "

val DIGIT_CHARS_REG get() = "0123456789"

val VN_CHARS_REG get() = "a-zA-ZÀÁÂÃÈÉÊẾÌÍÒÓÔÕÙÚĂĐĨŨƠàáâãèéêìíòóôõùúăđĩũơƯĂẠẢẤẦẨẪẬẮẰẲẴẶẸẺẼỀỀỂưăạảấầẩẫậắằẳẵặẹẻẽềềểỄỆỈỊỌỎỐỒỔỖỘỚỜỞỠỢỤỦỨỪễệỉịọỏốồổỗộớờởỡợụủứừỬỮỰỲỴÝỶỸửữựỳỵỷỹ "

val DIGIT_CHARS get() = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')

val USERNAME_CHARS
    get() = charArrayOf(
        'Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P', 'A', 'S', 'D', 'F', 'G', 'H', 'J',
        'K', 'L', 'Z', 'X', 'C', 'V', 'B', 'N', 'M',
        'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p', 'a', 's', 'd', 'f', 'g', 'h', 'j',
        'k', 'l', 'z', 'x', 'c', 'v', 'b', 'n', 'm',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    )

val VN_CHARS
    get() = charArrayOf(
        'Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P', 'A', 'S', 'D', 'F', 'G', 'H', 'J',
        'K', 'L', 'Z', 'X', 'C', 'V', 'B', 'N', 'M',
        'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p', 'a', 's', 'd', 'f', 'g', 'h', 'j',
        'k', 'l', 'z', 'x', 'c', 'v', 'b', 'n', 'm',

        'À', 'Á', 'Ả', 'Ã', 'Ạ', 'à', 'á', 'ả', 'ã', 'ạ',
        'Ă', 'Ằ', 'Ắ', 'Ẳ', 'Ẵ', 'Ặ', 'ă', 'ằ', 'ắ', 'ẳ', 'ẵ', 'ặ',
        'Â', 'Ầ', 'Ấ', 'Ẩ', 'Ẫ', 'Ậ', 'â', 'ầ', 'ấ', 'ẩ', 'ẫ', 'ậ',
        'È', 'É', 'Ẻ', 'Ẽ', 'Ẹ', 'è', 'é', 'ẻ', 'ẽ', 'ẹ',
        'Ê', 'Ề', 'Ế', 'Ể', 'Ễ', 'Ệ', 'ê', 'ề', 'ế', 'ể', 'ễ', 'ệ',
        'Ì', 'Í', 'Ỉ', 'Ĩ', 'Ị', 'ì', 'í', 'ỉ', 'ĩ', 'ị',

        'Ò', 'Ó', 'Ỏ', 'Õ', 'Ọ', 'ò', 'ó', 'ỏ', 'õ', 'ọ',
        'Ơ', 'Ờ', 'Ớ', 'Ở', 'Ỡ', 'Ợ', 'ơ', 'ờ', 'ớ', 'ở', 'ỡ', 'ợ',
        'Ô', 'Ồ', 'Ố', 'Ổ', 'Ỗ', 'Ộ', 'ô', 'ồ', 'ố', 'ổ', 'ỗ', 'ộ',
        'Ù', 'Ú', 'Ủ', 'Ũ', 'Ụ', 'ù', 'ú', 'ủ', 'ũ', 'ụ',
        'Ư', 'Ừ', 'Ứ', 'Ử', 'Ữ', 'Ự', 'ư', 'ừ', 'ứ', 'ử', 'ữ', 'ự',
        'Ỳ', 'Ý', 'Ỷ', 'Ỹ', 'Ỵ', 'ỳ', 'ý', 'ỷ', 'ỹ', 'ỵ',
        'Đ', 'đ',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ' '
    )

val nameMinLength get() = 1
val nameMaxLength get() = 100
val personNameError
    get() = "Tên chứa từ $nameMinLength-$nameMaxLength ký tự, được bao gồm ,.'-_ và có ít nhất 1 chữ cái"

val String?.isPersonName: Boolean
    get() {
        val s = this.trimText
        if (s.isNullOrEmpty()) return false
        return s.isLeastOnce(VN_CHARS_REG) &&
                s.length in nameMinLength..nameMaxLength
        /*&& s.isOnlyAllow("$VN_CHARS_REG$DIGIT_CHARS_REG$SPACE_CHAR,.'’\\-_") */
    }

val String?.hasOneNumber: Boolean
    get() {
        this ?: return false
        return this.matches(".*[0-9].*".toRegex())
    }

val Number?.convertToPrice: String
    get() {
        this ?: return ""
        return NumberFormat.getNumberInstance(
            Locale.US
        ).format(this).toString()
    }

val String?.hasOneUppercase: Boolean
    get() {
        this ?: return false
        return this.matches(".*[A-Z].*".toRegex())
    }
val String?.isEmail: Boolean
    get() {
        this ?: return false
        return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }

val String?.isPhoneNumber: Boolean get() = (this?.length ?: 0) > 9 && this?.firstOrNull() == '0'

val String?.isIdCardNumber: Boolean
    get() {
        val length = this?.length ?: 0
        return length == 9 || length == 12
    }

fun String?.isDate(fmt: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")): Boolean {
    this ?: return false
    return try {
        val date = fmt.parse(this)
        return fmt.format(date) == this
    } catch (e: Throwable) {
        false
    }
}