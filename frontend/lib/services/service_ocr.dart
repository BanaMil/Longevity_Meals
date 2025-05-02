// service_ocr.dart

import 'dart:io';
import 'package:http/http.dart' as http;
import 'package:path/path.dart' as p;
import 'api_constants.dart';

class OcrService {
  static Future<void> uploadImage({
    required String userId,
    required File imageFile,
  }) async {
    final uri = Uri.parse('${ApiConstants.baseUrl}/ocr/upload');

    final request = http.MultipartRequest('POST', uri)
      ..fields['userId'] = userId
      ..files.add(await http.MultipartFile.fromPath(
        'image',
        imageFile.path,
        filename: p.basename(imageFile.path),
      ));

    final response = await request.send();

    if (response.statusCode != 200) {
      throw Exception('이미지 업로드 실패: ${response.statusCode}');
    }
  }
}
 