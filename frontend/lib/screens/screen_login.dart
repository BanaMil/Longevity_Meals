// screen_login.dart

import 'dart:developer';
import 'package:flutter/material.dart';
import 'package:frontend/services/service_auth.dart';
import 'package:frontend/services/user_storage.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:frontend/screens/screen_health_info/choice_info.dart';
import 'package:frontend/screens/screen_index.dart';
import 'dart:convert';

class LoginScreen extends StatefulWidget {
  const LoginScreen({super.key});

  @override
  _LoginScreenState createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
  final _formKey = GlobalKey<FormState>();

  final TextEditingController useridController = TextEditingController();
  final TextEditingController passwordController = TextEditingController();

  void _login() async {
    final enteredId = useridController.text.trim();
    final password = passwordController.text.trim();

    log("🔐 Trying login with ID: '$enteredId', PW: '$password'");

    final response = await AuthService.loginUser(
      userid: enteredId,
      password: password,
    );

    if (!mounted) return;

    log('응답 statusCode: ${response.statusCode}');
    log('응답 body: ${response.body}'); // ← 여기서 JSON 전체 확인 가능

    try {
      final result = json.decode(response.body);

      if (response.statusCode == 200 &&
          result['success'] == true &&
          result['data'] != null) {
        final data = result['data'];
        final userId = data['userid'];
        final username = data['username'];
        final address = data['address'];
        final token = data['token'];
        final healthInfoSubmitted = data['healthInfoSubmitted'];

        final prefs = await SharedPreferences.getInstance();
        await prefs.setString('jwt_token', token);

        await UserStorage.saveUserInfo(
          username: username,
          userid: userId,
          address: address,
        );
        
        if (!mounted) return;

        // ScaffoldMessenger.of(
        //   context,
        // ).showSnackBar(SnackBar(content: Text("로그인 성공: ${result['message']}")));

        log("✅ 로그인 성공");

        // healthInfoSubmitted에 따라 화면 분기
        if (token != null && healthInfoSubmitted == true) {
          Navigator.pushReplacement(
            context,
            MaterialPageRoute(builder: (_) => const IndexScreen()),
          );
          log("🏠 건강정보 입력됨 → IndexScreen 이동");
        } else {
          Navigator.pushReplacement(
            context,
            MaterialPageRoute(builder: (_) => ChoiceInfoScreen()),
          );
          log("📋 건강정보 미입력 → ChoiceInfoScreen 이동");
        }
      } else {
        final msg = result['message'] ?? "로그인 실패 (원인 불명)";

        if (response.statusCode == 401) {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text("아이디나 비밀번호가 잘못 입력되었습니다.")),
          );
        } else {
          ScaffoldMessenger.of(
            context,
          ).showSnackBar(SnackBar(content: Text("❌ 로그인 실패: $msg")));
        }
        log("❌ 로그인 실패: $msg");
      }
    } catch (e) {
      log("🚨 로그인 중 JSON 파싱 또는 예외 발생: $e");
      ScaffoldMessenger.of(
        context,
      ).showSnackBar(SnackBar(content: Text("로그인 중 문제가 발생했습니다.")));
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color.fromARGB(255, 251, 255, 228),
      appBar: AppBar(
        // title: Text("로그인", style: TextStyle(fontWeight: FontWeight.bold)),
        backgroundColor: const Color.fromARGB(255, 251, 255, 228),
      ),
      body: SafeArea(
        child: SingleChildScrollView(
          child: Padding(
            padding: EdgeInsets.only(
              bottom: MediaQuery.of(context).viewInsets.bottom + 16,
            ),
            child: Form(
              key: _formKey,
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.center,
                children: [
                  Center(
                    child: ClipOval(
                      child: Image.asset(
                        'assets/images/longevity_meals_logo.png',
                        width: 150,
                        height: 150,
                        fit: BoxFit.cover,
                      ),
                    ),
                  ),
                  const SizedBox(height: 50),

                  SizedBox(
                    width: MediaQuery.of(context).size.width * 0.6,
                    child: TextFormField(
                      controller: useridController,
                      decoration: InputDecoration(labelText: '아이디'),
                      validator:
                          (value) => value!.isEmpty ? '아이디를 입력하세요' : null,
                    ),
                  ),
                  const SizedBox(height: 25),

                  SizedBox(
                    width: MediaQuery.of(context).size.width * 0.6,
                    child: TextFormField(
                      controller: passwordController,
                      decoration: InputDecoration(labelText: '비밀번호'),
                      validator:
                          (value) => value!.isEmpty ? '비밀번호를 입력하세요' : null,
                      obscureText: true,
                    ),
                  ),
                  const SizedBox(height: 50),
                  ElevatedButton(
                    onPressed: () async {
                      if (_formKey.currentState!.validate()) {
                        _login();
                      }
                    },
                    style: ElevatedButton.styleFrom(
                      backgroundColor: const Color.fromARGB(255, 196, 215, 110),
                      minimumSize: const Size(150, 50), // 버튼 높이
                      textStyle: const TextStyle(fontSize: 20), // 폰트 크기
                    ),
                    child: Text(
                      '로그인',
                      style: TextStyle(
                        color: Colors.black,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                  ),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }
}
