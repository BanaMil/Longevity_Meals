// screen_register.dart
import 'package:flutter/material.dart';
import 'package:frontend/services/service_auth.dart';
import 'package:frontend/utils/validators.dart';
import 'screen_login.dart';
import 'dart:convert';
import 'package:frontend/services/user_storage.dart';

class RegisterScreen extends StatefulWidget {
  const RegisterScreen({super.key});

  @override
  _RegisterScreenState createState() => _RegisterScreenState();
}

class _RegisterScreenState extends State<RegisterScreen> {
  final _formKey = GlobalKey<FormState>();

  final TextEditingController usernameController = TextEditingController();
  final TextEditingController idController = TextEditingController();
  final TextEditingController passwordController = TextEditingController();
  final TextEditingController confirmpasswordController = TextEditingController();
  final TextEditingController phoneController = TextEditingController();
  final TextEditingController addressController = TextEditingController();


  DateTime? _selectedBirthdate; // 생년월일 저장용

  void _pickBirthdate() async {
    final DateTime? picked = await showDatePicker(
      context: context,
      initialDate: DateTime(2000), // 기본 선택 날짜
      firstDate: DateTime(1900),
      lastDate: DateTime.now(),
      locale: const Locale("ko", "KR"), // 한국어로 표시되게
    );
    if (picked != null) {
      setState(() {
        _selectedBirthdate = picked;
      });
    }
  }

  bool isIdAvailable = false; //ID 사용 가능 여부 상태

  void _checkIdDuplicate() async {
    final id = idController.text.trim();

    if (id.isEmpty) {
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text("아이디를 입력해주세요.")),
      );
      return;
    }

    final response = await AuthService.checkIdDuplicate(id);

    if (!mounted) return;

    if (response.statusCode == 200) {
      final result = json.decode(response.body);
      final available = result['available'] == true;

      setState(() {
        isIdAvailable = available;
      });
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(available ? "사용 가능한 아이디입니다." : "이미 존재하는 아이디입니다.")),
      );
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text("아이디 확인 실패: ${response.body}")),
      );
    }
  }

  void _register() async {
    final username = usernameController.text;
    final id = idController.text;
    final password = passwordController.text;
    final confirmpassword = confirmpasswordController.text;
    final birthdate = _selectedBirthdate == null
        ? null
        : '${_selectedBirthdate!.year.toString().padLeft(4, '0')}-'
        '${_selectedBirthdate!.month.toString().padLeft(2, '0')}-'
        '${_selectedBirthdate!.day.toString().padLeft(2, '0')}';
    final phone = phoneController.text.trim();
    final address = addressController.text.trim();

    // 비밀번호 확인 검사
    if (password != confirmpassword) {
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text("비밀번호가 일치하지 않습니다.")),
      );
      return;
    }

    // 생년월일 선택 확인 검사
    if (birthdate == null) {
      if(!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text("생년월일을 선택해주세요.")),
      );
      return;
    }

    final response = await AuthService.registerUser(
      username: username,
      id: id,
      password: password,
      birthdate: birthdate,
      phone: phone,
      address: address,
    );

    if (!mounted) return;

    if (response.statusCode == 200) {
      final result = json.decode(response.body);

      // SharedPreferences에 저장
      await UserStorage.saveUserInfo(
        username: username,
        id: id,
        address: address,
      );

      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text("회원가입 성공: ${result['message']}")),
      );
      Navigator.pushReplacement(
        context,
        MaterialPageRoute(builder: (_) => LoginScreen()),
      );
    } else {
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text("회원가입 실패: ${response.body}")),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    String birthdateText = _selectedBirthdate == null
        ? '생년월일을 선택해주세요'
        : '${_selectedBirthdate!.year}-${_selectedBirthdate!.month.toString().padLeft(2, '0')}-${_selectedBirthdate!.day.toString().padLeft(2,'0')}';

    return Scaffold(
      appBar: AppBar(title: Text("회원가입")),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Form(
          key: _formKey,
          child: Column(
            children: [
              TextFormField(
                controller: usernameController,
                decoration: InputDecoration(labelText: '이름'),
                validator: Validators.validateUsername,
              ),
              TextFormField(
                controller: idController,
                decoration: InputDecoration(labelText: '아이디'),
                validator: Validators.validateId,
              ),
              SizedBox(height: 8),
              ElevatedButton(
                onPressed: _checkIdDuplicate,
                child: Text('아이디 중복 확인'),
              ),
              TextFormField(
                controller: passwordController,
                obscureText: true,
                decoration: InputDecoration(labelText: '비밀번호'),
                validator: Validators.validatePassword,
              ),
              TextField(
                  controller: confirmpasswordController,
                  obscureText: true,
                  decoration: InputDecoration(labelText: '비밀번호 확인')),
              SizedBox(height: 12),
              InkWell(
                onTap: _pickBirthdate,
                child: InputDecorator(
                  decoration: InputDecoration(
                    labelText: '생년월일',
                    border: OutlineInputBorder(),
                  ),
                  child: Text(birthdateText),
                ),
              ),
              TextFormField(
                controller: phoneController,
                decoration: InputDecoration(labelText: '휴대폰 번호'),
                keyboardType: TextInputType.phone,
                validator: (value) {
                  if (value == null || value.isEmpty) {
                    return '휴대폰 번호를 입력해주세요.';
                  }
                  return null;
                },
              ),
              TextFormField(
                controller: addressController,
                decoration: InputDecoration(labelText: '주소'),
                validator: (value) {
                  if (value == null || value.isEmpty) {
                    return '주소를 입력해주세요.';
                  }
                  return null;
                },
              ),
              SizedBox(height: 20),
              ElevatedButton(
                onPressed: () async {
                  if (_formKey.currentState!.validate()) {
                    _register();
                  }
                },
                child: Text('회원가입'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
