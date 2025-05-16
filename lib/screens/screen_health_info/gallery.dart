import 'dart:io';
import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:frontend/services/service_ocr.dart';
import 'package:frontend/screens/screen_health_info/input_diseases.dart';

class GalleryScreen extends StatefulWidget {
  const GalleryScreen({super.key});

  @override
  State<GalleryScreen> createState() => _GalleryScreenState();
}

class _GalleryScreenState extends State<GalleryScreen> {
  final picker = ImagePicker();
  File? _selectedImage;
  String? _userid;

  @override
  void initState() {
    super.initState();
    _loadUserId(); // 먼저 ID 로딩 후 이미지 선택
  }

  Future<void> _loadUserId() async {
    final prefs = await SharedPreferences.getInstance();
    final storedId = prefs.getString('userid');

    if (storedId == null) {
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text("사용자 ID를 찾을 수 없습니다.")),
      );
      Navigator.pop(context);
      return;
    }

    setState(() {
      _userid = storedId;
    });

    _pickImageFromGallery();
  }

  Future<void> _pickImageFromGallery() async {
    final pickedFile = await picker.pickImage(source: ImageSource.gallery);

    if (pickedFile != null) {
      setState(() {
        _selectedImage = File(pickedFile.path);
      });
    } else {
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text("이미지가 선택되지 않았습니다.")),
      );
      Navigator.pop(context);
    }
  }

  Future<void> _uploadImage() async {
    if (_selectedImage == null || _userid == null) return;

    try {
      await OcrService.uploadImage(
        userId: _userid!,
        imageFile: _selectedImage!,
      );

      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text("업로드 완료")),
      );

      Navigator.push(
        context,
        MaterialPageRoute(builder: (_) => InputDiseasesScreen()),
      );
    } catch (e) {
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text("업로드 실패: $e")),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('갤러리에서 선택')),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            _selectedImage != null
                ? Image.file(_selectedImage!)
                : const CircularProgressIndicator(),
            const SizedBox(height: 20),
            ElevatedButton(
              onPressed: _uploadImage,
              child: const Text("업로드 및 다음"),
            ),
          ],
        ),
      ),
    );
  }
}
