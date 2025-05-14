// screen_login.dart

import 'package:flutter/material.dart';
import 'package:frontend/services/service_auth.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:frontend/screens/screen_health_info/choice_info.dart';
import 'package:frontend/tabs/tab_home.dart';
// import 'screen_home.dart';
import 'dart:convert';


class LoginScreen extends StatefulWidget {
  const LoginScreen({super.key});

  @override
  _LoginScreenState createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
  final _formKey = GlobalKey<FormState>();

  final TextEditingController idController = TextEditingController();
  final TextEditingController passwordController = TextEditingController();
  
  /*void _login() async {
    final enteredId = idController.text.trim();
    final password = passwordController.text.trim();

    final response = await AuthService.loginUser(id: enteredId, password: password);

    if (!mounted) return;

    if (response.statusCode == 200) {
      final result = json.decode(response.body);
/*
      if (!result.containsKey('data') || result['data'] == null) {
        print("⚠️ 로그인 실패: 서버 응답에 data가 없음");
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text("로그인 실패: ${result['message']}")),
        );
        return;
      }
 */
      final data = result['data'];

      final userId = data['id'];
      final username = data['username'];
      final address = data['address'];
      final token = data['token'];


      // 토큰 저장
      final prefs = await SharedPreferences.getInstance();
      await prefs.setString('jwt_token', token);
      await prefs.setString('username', username);
      await prefs.setString('id', userId);  // 로그인한 사용자 ID 
      await prefs.setString('address', address);

      if (!mounted) return;

      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text("로그인 성공: ${result['message']}")),
      );
      print("로그인 성공");
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text("로그인 실패: ${response.body}")),
      );
      print("로그인 실패");
    }
  }

   */

  void _login() async { // GPT 코드
    final enteredId = idController.text.trim();
    final password = passwordController.text.trim();

    print("🔐 Trying login with ID: '$enteredId', PW: '$password'");

    final response = await AuthService.loginUser(id: enteredId, password: password);

    if (!mounted) return;

    print('응답 statusCode: ${response.statusCode}');
    print('응답 body: ${response.body}');  // ← 여기서 JSON 전체 확인 가능

    try {
      final result = json.decode(response.body);

      if (response.statusCode == 200 && result['success'] == true && result['data'] != null) {
        final data = result['data'];
        final userId = data['id'];
        final username = data['username'];
        final address = data['address'];
        final token = data['token'];
        final healthInfoSubmitted = data['healthInfoSubmitted'];

        final prefs = await SharedPreferences.getInstance();
        await prefs.setString('jwt_token', token);
        await prefs.setString('username', username);
        await prefs.setString('id', userId);
        await prefs.setString('address', address);

        if (!mounted) return;

        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text("로그인 성공: ${result['message']}")),
        );
        print("✅ 로그인 성공");
        
        // ✅ healthInfoSubmitted에 따라 화면 분기
        if (healthInfoSubmitted == true) {
          Navigator.pushReplacement(
            context,
            MaterialPageRoute(builder: (_) => HomeTab()),  // 홈 화면으로 이동
          );

          print("🏠 건강정보 입력됨 → tab_home 이동");
        } else {
          Navigator.pushReplacement(
            context,
            MaterialPageRoute(builder: (_) => ChoiceInfoScreen()),  // 건강정보 입력 화면
          );
          print("📋 건강정보 미입력 → ChoiceInfoScreen 이동");
        }
      } else {
        final msg = result['message'] ?? "로그인 실패 (원인 불명)";
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text("❌ 로그인 실패: $msg")),
        );
        print("❌ 로그인 실패: $msg");
      }
    } catch (e) {
      print("🚨 로그인 중 JSON 파싱 또는 예외 발생: $e");
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text("로그인 중 문제가 발생했습니다.")),
      );
    }
  }


  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text("로그인 화면")),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Form(
          key: _formKey,
          child: Column(
            children: [
              TextFormField(
                controller: idController,
                decoration: InputDecoration(labelText: '아이디'),
                validator: (value) => value!.isEmpty ? '아이디를 입력하세요' : null,
              ),
              TextFormField(
                controller: passwordController,
                decoration: InputDecoration(labelText: '비밀번호'),
                validator: (value) => value!.isEmpty ? '비밀번호를 입력하세요' : null,
                obscureText: true,
              ),
              SizedBox(height: 20),
              ElevatedButton(
                onPressed: _login,
                style: ElevatedButton.styleFrom(
                  minimumSize: const Size(200, 60),           // 버튼 높이 
                  textStyle: const TextStyle(fontSize: 20),   // 폰트 크기
                ),
                child: Text('로그인'),
              ),
            ],
          ),
        ),  
      ),    
    );
  }
}