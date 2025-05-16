// health_info_provider.dart
// 건강정보를 앱 전체에서 기억하고 어느 화면에서든 이 값을 읽거나 수정할 수 있도록 하는 클래스

import 'package:flutter/material.dart';
import 'package:frontend/models/health_info.dart';
import 'package:frontend/services/service_health_info.dart';

class HealthInfoProvider with ChangeNotifier {
  final HealthInfo _info = HealthInfo();
  HealthInfo get info => _info;

  void setHeight(double h) { _info.height = h; notifyListeners(); }
  void setWeight(double w) { _info.weight = w; notifyListeners(); }
  void toggleDislike(String v) => _toggleList(_info.dislikes, v);

  void setUserId(String userid) {
    _info.userid = userid;
    notifyListeners();
  }

  // 비선호 음식은 정해진 리스트에서 선택하는 것이 아닌 사용자가 입력하는 것이기 때문에 중복확인을 따로 해야한다.
  void addDislike(String v) {
    if (!_info.dislikes.contains(v)) {
      _info.dislikes.add(v);
      notifyListeners();
    }
  }
  void removeDislike(String v) {
    _info.dislikes.remove(v);
    notifyListeners();
  }
   
  void _toggleList(List<String> list, String v) {
    list.contains(v) ? list.remove(v) : list.add(v);
    notifyListeners();
  }

  void toggleDiseaseAllergy(String v, List<String> targetList) {
    if (v == '해당없음') {
      targetList
        ..clear()
        ..add('해당없음');
    } else {
      if (targetList.contains(v)) {
        targetList.remove(v);
      } else {
        targetList.remove('해당없음');
        targetList.add(v);
      }
    }
    notifyListeners();
  }

  Future<void> submitToServer() async {
    await HealthInfoService.uploadHealthInfo(_info);
  } 
}

