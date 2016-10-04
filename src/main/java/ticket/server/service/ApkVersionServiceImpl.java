package ticket.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ticket.server.model.apk.ApkVersion;
import ticket.server.repository.apk.ApkVersionRepository;

@Service
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class ApkVersionServiceImpl implements ApkVersionService {

	@Autowired
	ApkVersionRepository apkVersionRepository;

	@Override
	public ApkVersion findById(Long id) {
		return apkVersionRepository.findOne(id);
	}

}
