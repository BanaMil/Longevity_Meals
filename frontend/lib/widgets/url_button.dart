// url_button.dart
// test용 

import 'package:flutter/material.dart';
import 'package:url_launcher/url_launcher.dart';

class UrlButton extends StatelessWidget {
  final String url;

  const UrlButton({super.key, required this.url});

  Future<void> _launchUrl() async {
    final Uri uri = Uri.parse(url);
    if (await canLaunchUrl(uri)) {
      await launchUrl(uri, mode: LaunchMode.externalApplication);
    } else {
      throw 'Could not launch $url';
    }
  }

  @override
  Widget build(BuildContext context) {
    return ElevatedButton.icon(
      onPressed: _launchUrl,
      icon: const Icon(Icons.image),
      label: const Text('사진 보기'),
      style: ElevatedButton.styleFrom(
        backgroundColor: Colors.lightGreen[200],
      ),
    );
  }
}
