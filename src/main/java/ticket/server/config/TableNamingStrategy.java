package ticket.server.config;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

/**
 * mysql在Linux中表名默认为小写 改变表名的大小写
 * 
 * @author chenh
 *
 */
public class TableNamingStrategy extends PhysicalNamingStrategyStandardImpl {

	private static final long serialVersionUID = -4264175238784729886L;

	@Override
	public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment context) {
		return new Identifier(name.getText().toLowerCase(), name.isQuoted());
	}

}
