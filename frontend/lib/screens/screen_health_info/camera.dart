// camera.dart

import 'dart:io'; // 파일, 네트워크 입출력
import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:frontend/services/service_ocr.dart';
import 'package:frontend/screens/screen_health_info/input_diseases.dart';

class CameraScreen extends StatefulWidget {
  const CameraScreen({super.key});

  @override
  State<CameraScreen> createState() => _CameraScreenState();
}

class _CameraScreenState extends State<CameraScreen> {
  final picker = ImagePicker();
  File? _capturedImage;
  String? _id;

  @override
  void initState() {
    super.initState();
    _loadUserId(); // 먼저 ID를 로드한 후 사진 촬영 실행 
  }

  Future<void> _loadUserId() async {
    final prefs = await SharedPreferences.getInstance();
    final storedId = prefs.getString('id');

    if (storedId == null) {
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('사용자 ID를 찾을 수 없습니다')),
      );
      Navigator.pop(context);
      return;
    }
    
    setState(() {
      _id = storedId;
    });

    _takePicture();
  }

  Future<void> _takePicture() async {
    final pickedFile = await picker.pickImage(source: ImageSource.camera);
      
    if (pickedFile != null) {
      setState(() {
        _capturedImage = File(pickedFile.path);
      });

      try {
        await OcrService.uploadImage(
          userId: _id!, //
          imageFile: _capturedImage!,
        );

        if (!mounted) return;
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('업로드 완료')),
        );

        Navigator.push(context, MaterialPageRoute(builder: (_) => InputDiseasesScreen())); 
      } catch (e) {
        if (!mounted) return;
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('업로드 실패: $e')),
        );
      }
    } else {
      if (!mounted) return;
      Navigator.pop(context);
      }
  }
 
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('촬영 및 업로드드')),
      body: Center(
        child: _capturedImage != null
          ? Image.file(_capturedImage!)
          : const CircularProgressIndicator(), 
      ),
    );
  }
}