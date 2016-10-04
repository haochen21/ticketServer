package ticket.server.service;

import ticket.server.model.apk.ApkVersion;

public interface ApkVersionService {

	ApkVersion findById(Long id);
}
