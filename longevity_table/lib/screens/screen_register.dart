// screen_register.dart
import 'package:flutter/material.dart';
import 'package:longevity_table/services/api_service.dart';
import 'dart:convert';

class RegisterScreen extends StatefulWidget {
  @override
  _RegisterScreenState createState() => _RegisterScreenState();
}

class _RegisterScreenState extends State<RegisterScreen> {
  final TextEditingController usernameController = TextEditingController();
  final TextEditingController idController = TextEditingController();
  final TextEditingController passwordController = TextEditingController();
  final TextEditingController confirmpasswordController = TextEditingController();

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

  void _register() async {
    final username = usernameController.text;
    final id = idController.text;
    final password = passwordController.text;
    final confirmpassword = confirmpasswordController.text;
    final birthdate = _selectedBirthdate?.toIso8601String(); // ISO 포맷으로 변환환

    // 비밀번호 확인 검사
    if (password != confirmpassword) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text("비밀번호가 일치하지 않습니다.")),
      );
      return;
    }

    // 생년월일 선택 확인 검사
    if (birthdate == null) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text("생년월일을 선택해주세요.")),
      );
      return;
    }

    final response = await ApiService.registerUser(
      username: username,
      id: id,
      password: password,
      birthdate: birthdate,
    );
  
    if (response.statusCode == 200) {
      final result = json.decode(response.body);
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text("회원가입 성공: ${result['message']}")),
      );
    } else {
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
        child: Column(
          children: [
            TextField(
              controller: usernameController, 
              decoration: InputDecoration(labelText: '이름')),
            TextField(
              controller: idController,
              decoration: InputDecoration(labelText: '아이디')),
            TextField(
              controller: passwordController, 
              obscureText: true,
              decoration: InputDecoration(labelText: '비밀번호')),
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
            SizedBox(height: 20),
            ElevatedButton(
              onPressed: _register,
              child: Text('회원가입'),
            ),
          ],
        ),
      ),
    );
  }
}





