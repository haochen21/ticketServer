package ticket.server.repository.security;

import ticket.server.model.security.Merchant;

public interface MerchantRepositoryCustom {

	Merchant login(String login);
}
