class Validators {
  // 사용자 이름 (username) 유효성 검사
  static String? validateUsername(String? value) {
    if (value == null || value.isEmpty) {
      return '이름을 입력하세요.';
    }
    if (value.length < 2 || value.length > 10) {
      return '이름은 2자 이상 10자 이하로 입력하세요.';
    }
    return null;
  }

  // 아이디 (id) 유효성 검사: 영문자와 숫자, 4~16자
  static String? validateId(String? value) {
    final regex = RegExp(r'^[a-zA-Z0-9]{4,16}$');
    if (value == null || value.isEmpty) {
      return '아이디를 입력하세요.';
    }
    if (!regex.hasMatch(value)) {
      return '아이디는 4~16자의 영문자와 숫자로 입력하세요.';
    }
    return null;
  }

  // 비밀번호 (password) 유효성 검사: 8자 이상, (숫자/문자 조합 권장)
  static String? validatePassword(String? value) {
    if (value == null || value.isEmpty) {
      return '비밀번호를 입력하세요.';
    }
    if (value.length < 8) {
      return '비밀번호는 8자 이상이어야 합니다.';
    }
    return null;
  }
}
