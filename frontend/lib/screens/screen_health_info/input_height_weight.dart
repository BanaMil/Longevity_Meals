// input_height_weight.dart

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:frontend/providers/health_info_provider.dart';
import 'input_diseases.dart';

class InputHeightWeightScreen extends StatefulWidget {
  const InputHeightWeightScreen({super.key});

  @override
  State<InputHeightWeightScreen> createState() =>
      _InputHeightWeightScreenState();
}


class _InputHeightWeightScreenState extends State<InputHeightWeightScreen> {
  final _formKey = GlobalKey<FormState>();
  final heightController = TextEditingController();
  final weightController = TextEditingController();
  String? selectedGender;
  bool showGenderError = false;

  @override
<<<<<<< HEAD
  void dispose() {
    heightController.dispose();
    weightController.dispose();
    super.dispose();
  }

  @override
=======
>>>>>>> 2ce09920618cf71c178c4f72fdbb3a69ba8eb7db
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('건강정보 입력'),
        actions: [
          IconButton(
            icon: const Icon(Icons.arrow_forward),
            onPressed: () {
              final isValid = _formKey.currentState?.validate() ?? false;
              if (!isValid || selectedGender == null) {
<<<<<<< HEAD
                setState(() => showGenderError = selectedGender == null);
=======
                setState(() {
                  showGenderError = selectedGender == null;
                });
>>>>>>> 2ce09920618cf71c178c4f72fdbb3a69ba8eb7db
                return;
              }
              final h = double.parse(heightController.text);
              final w = double.parse(weightController.text);
              context.read<HealthInfoProvider>().setGender(selectedGender!);
              context.read<HealthInfoProvider>().setHeight(h);
              context.read<HealthInfoProvider>().setWeight(w);

              Navigator.push(
                context,
<<<<<<< HEAD
                MaterialPageRoute(builder: (_) => const InputDiseasesScreen()),
=======
                MaterialPageRoute(
                  builder: (context) => const InputDiseasesScreen(),
                ),
>>>>>>> 2ce09920618cf71c178c4f72fdbb3a69ba8eb7db
              );
            },
          ),
        ],
      ),
<<<<<<< HEAD
=======

>>>>>>> 2ce09920618cf71c178c4f72fdbb3a69ba8eb7db
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: Form(
          key: _formKey,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
<<<<<<< HEAD
=======
              // 성별 선택
>>>>>>> 2ce09920618cf71c178c4f72fdbb3a69ba8eb7db
              const Padding(
                padding: EdgeInsets.symmetric(vertical: 8),
                child: Text('성별을 선택해주세요.'),
              ),
<<<<<<< HEAD
              SegmentedButton<String>(
                segments: const [
                  ButtonSegment(value: 'male', label: Text('남성'), icon: Icon(Icons.male)),
                  ButtonSegment(value: 'female', label: Text('여성'), icon: Icon(Icons.female)),
                ],
                selected: selectedGender == null ? <String>{} : {selectedGender!},
                onSelectionChanged: (s) {
                  // 빈 선택 허용 시, 사용자가 다시 탭해 비울 수 있으므로 가드
                  setState(() {
                    selectedGender = s.isEmpty ? null : s.first;
                    showGenderError = false;
                  });
                },
                emptySelectionAllowed: true,
=======
              Row(
                children: [
                  Radio<String>(
                    value: 'male',
                    groupValue: selectedGender,
                    onChanged: (value) {
                      setState(() {
                        selectedGender = value;
                      });
                    },
                  ),
                  const Text('남성'),
                  Radio<String>(
                    value: 'female',
                    groupValue: selectedGender,
                    onChanged: (value) {
                      setState(() {
                        selectedGender = value;
                      });
                    },
                  ),
                  const Text('여성'),
                ],
>>>>>>> 2ce09920618cf71c178c4f72fdbb3a69ba8eb7db
              ),
              if (showGenderError)
                const Padding(
                  padding: EdgeInsets.only(top: 4),
<<<<<<< HEAD
                  child: Text('성별을 선택해주세요.', style: TextStyle(color: Colors.red)),
                ),

=======
                  child: Text(
                    '성별을 선택해주세요.',
                    style: TextStyle(color: Colors.red),
                  ),
                ),
              // 키 입력
>>>>>>> 2ce09920618cf71c178c4f72fdbb3a69ba8eb7db
              const SizedBox(height: 12),
              const Padding(
                padding: EdgeInsets.symmetric(vertical: 8),
                child: Text('키를 입력해주세요.'),
              ),
              TextFormField(
                controller: heightController,
                decoration: const InputDecoration(labelText: '키 (cm)'),
<<<<<<< HEAD
                keyboardType: const TextInputType.numberWithOptions(decimal: true),
                validator: (value) {
                  final parsed = double.tryParse(value ?? '');
                  if (parsed == null || parsed <= 0) return '유효한 키(cm)를 입력해주세요.';
                  return null;
                },
              ),

=======
                keyboardType: TextInputType.number,
                validator: (value) {
                  final parsed = double.tryParse(value ?? '');
                  if (parsed == null || parsed <= 0) {
                    return '유효한 키(cm)를 입력해주세요.';
                  }
                  return null;
                },
              ),
              // 몸무게 입력
>>>>>>> 2ce09920618cf71c178c4f72fdbb3a69ba8eb7db
              const SizedBox(height: 12),
              const Padding(
                padding: EdgeInsets.symmetric(vertical: 8),
                child: Text('몸무게를 입력해주세요.'),
              ),
              TextFormField(
                controller: weightController,
                decoration: const InputDecoration(labelText: '몸무게 (kg)'),
<<<<<<< HEAD
                keyboardType: const TextInputType.numberWithOptions(decimal: true),
                validator: (value) {
                  final parsed = double.tryParse(value ?? '');
                  if (parsed == null || parsed <= 0) return '유효한 몸무게(kg)를 입력해주세요.';
=======
                keyboardType: TextInputType.number,
                validator: (value) {
                  final parsed = double.tryParse(value ?? '');
                  if (parsed == null || parsed <= 0) {
                    return '유효한 몸무게(kg)를 입력해주세요.';
                  }
>>>>>>> 2ce09920618cf71c178c4f72fdbb3a69ba8eb7db
                  return null;
                },
              ),
            ],
          ),
        ),
      ),
    );
  }
}
