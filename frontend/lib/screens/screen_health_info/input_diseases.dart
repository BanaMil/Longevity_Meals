// input_diseases.dart

import 'dart:collection';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:frontend/providers/health_info_provider.dart';
import 'package:frontend/widgets/selectable_button.dart';
import 'input_allergies.dart';

class InputDiseasesScreen extends StatelessWidget {
  const InputDiseasesScreen({super.key});

<<<<<<< HEAD
  static const List<String> _diseases = [
=======
  final List<String> diseaseList = const [
>>>>>>> 2ce09920618cf71c178c4f72fdbb3a69ba8eb7db
    '간경변증',
    '갑상선 기능저하증',
    '갑상선 기능항진증',
    '고지혈증',
    '고혈압',
    '골다공증',
    '근감소증',
    '기관지천식',
    '뇌졸증',
    '뇌출혈',
    '당뇨병',
    '동맥경화증',
    '대장암',
    '만성 신부전',
    '만성 폐쇄성 폐질환',
    '말초신경병증',
    '부신기능저하증',
    '부정맥',
    '비만',
    '신증후군',
    '심근경색',
    '심부전',
    '요로결석',
    '요통',
    '유방암',
    '위식도역류질환',
    '위암',
    '위염',
    '저혈당',
    '전립선암',
    '지방간',
    '치매',
    '췌장염',
    '통풍',
    '퇴행성 관절염',
    '파킨슨병',
    '폐렴',
    '폐암',
    '협심증',
    '해당없음',
  ];

  // 초성 테이블
  static const List<String> _choseong = [
    'ㄱ',
    'ㄲ',
    'ㄴ',
    'ㄷ',
    'ㄸ',
    'ㄹ',
    'ㅁ',
    'ㅂ',
    'ㅃ',
    'ㅅ',
    'ㅆ',
    'ㅇ',
    'ㅈ',
    'ㅉ',
    'ㅊ',
    'ㅋ',
    'ㅌ',
    'ㅍ',
    'ㅎ'
  ];

  static const List<String> _indexOrder = [
    'ㄱ',
    'ㄲ',
    'ㄴ',
    'ㄷ',
    'ㄸ',
    'ㄹ',
    'ㅁ',
    'ㅂ',
    'ㅃ',
    'ㅅ',
    'ㅆ',
    'ㅇ',
    'ㅈ',
    'ㅉ',
    'ㅊ',
    'ㅋ',
    'ㅌ',
    'ㅍ',
    'ㅎ',
    '#'
  ];

  // 초성 추출 함수
  String _firstChoseong(String text) {
    if (text.isEmpty) return '#';
    final int code = text.codeUnitAt(0);
    if (code < 0xAC00 || code > 0xD7A3) return '#'; 
    final int sIndex = code - 0xAC00;
    final int ci = sIndex ~/ (21 * 28);
    return _choseong[ci];
  }

  // 초성 그룹핑 + 가나다 정렬
  SplayTreeMap<String, List<String>> _groupByChoseong(List<String> items) {
    final map = <String, List<String>>{};
    for (final d in items) {
      if (d == '해당없음') continue;
      final key = _firstChoseong(d);
      map.putIfAbsent(key, () => []).add(d);
    }

    for (final e in map.entries) {
      e.value.sort((a, b) => a.compareTo(b));
    }

    // 초성 순서에 맞게 정렬된 맵 반환
    final sorted = SplayTreeMap<String, List<String>>(
      (a, b) {
        final ai = _indexOrder.indexOf(a);
        final bi = _indexOrder.indexOf(b);
        final aa = ai == -1 ? 999 : ai;
        final bb = bi == -1 ? 999 : bi;
        if (aa != bb) return aa.compareTo(bb);
        return a.compareTo(b);
      },
    )..addAll(map);

    return sorted;
  }

  @override
  Widget build(BuildContext context) {
    final provider = context.watch<HealthInfoProvider>();

    final grouped = _groupByChoseong(_diseases);
    final hasNoDisease = _diseases.contains('해당없음');

    return Scaffold(
      appBar: AppBar(
        title: const Text('질병 선택'),
        actions: [
          IconButton(
            icon: const Icon(Icons.arrow_forward),
            onPressed: () {
              Navigator.push(
                context,
                MaterialPageRoute(
                  builder: (context) => const InputAllergiesScreen(),
                ),
              );
            },
          ),
        ],
      ),
<<<<<<< HEAD
      body: ListView(
        padding: const EdgeInsets.only(bottom: 20),
        children: [
          const Padding(
            padding: EdgeInsets.all(16),
            child: Text(
              '질병을 선택해주세요.',
              style: TextStyle(fontSize: 20, fontWeight: FontWeight.w700),
            ),
          ),

          if (hasNoDisease)
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
              child: SelectableButton(
                text: '해당없음',
                isSelected: provider.info.diseases.contains('해당없음'),
                onTap: () => provider.toggleDiseaseAllergy(
                  '해당없음',
                  provider.info.diseases,
                ),
                fontSize: 15,
              ),
            ),

          // 초성 섹션 렌더링
          ...grouped.entries.map(
            (entry) => _ChoseongSection(
              title: entry.key,
              diseases: entry.value,
              isSelected: (s) => provider.info.diseases.contains(s),
              onTap: (s) => provider.toggleDiseaseAllergy(
                s,
                provider.info.diseases,
              ),
            ),
          ),         
=======

      body: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Padding(
            padding: EdgeInsets.all(16),
            child: Text('질병을 선택해주세요.'),
          ),

          Expanded(
            child: Padding(
              padding: const EdgeInsets.symmetric(horizontal: 16),
              child: GridView.count(
                crossAxisCount: 3, // 3열 고정
                mainAxisSpacing: 12, // 세로 간격
                crossAxisSpacing: 8, //가로 간격
                childAspectRatio: 2.8, // 버튼 가로/세로 비율 조정
                children:
                    diseaseList.map((d) {
                      final selected = provider.info.diseases.contains(d);
                      return SelectableButton(
                        text: d,
                        isSelected: selected,
                        onTap:
                            () => provider.toggleDiseaseAllergy(
                              d,
                              provider.info.diseases,
                            ),
                      );
                    }).toList(),
              ),
            ),
          ),
>>>>>>> 2ce09920618cf71c178c4f72fdbb3a69ba8eb7db
        ],
      ),
    );
  }
}
<<<<<<< HEAD

class _ChoseongSection extends StatelessWidget {
  final String title;
  final List<String> diseases;
  final bool Function(String) isSelected;
  final void Function(String) onTap;

  const _ChoseongSection({
    required this.title,
    required this.diseases,
    required this.isSelected,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.only(top: 10),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // 섹션 헤더
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 6),
            child: Text(
              title,
              style: Theme.of(context).textTheme.titleMedium?.copyWith(
                    fontWeight: FontWeight.w700,
                  ),
            ),
          ),

          // 버튼 그리드
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 12),
            child: GridView.builder(
              shrinkWrap: true,
              physics: const NeverScrollableScrollPhysics(),
              itemCount: diseases.length,
              gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                crossAxisCount: 3, // 3열 고정
                crossAxisSpacing: 8, //가로 간격
                mainAxisSpacing: 12, // 세로 간격
                childAspectRatio: 2.8, // 버튼 가로/세로 비율 조정
              ),
              itemBuilder: (context, i) {
                final d = diseases[i];
                return SelectableButton(
                  text: d,
                  isSelected: isSelected(d),
                  onTap: () => onTap(d),
                );
              },
            ),
          ),
          const SizedBox(height: 12),
          const Divider(thickness: 1),
        ],
      ),
    );
  }
}
=======
>>>>>>> 2ce09920618cf71c178c4f72fdbb3a69ba8eb7db
