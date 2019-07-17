package rpg.pojo;

/**用户buff类
 * @author ljq
 *
 */
public class Userbuff {
    private Integer id;

    private String username;

    private String buff;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public String getBuff() {
        return buff;
    }

    public void setBuff(String buff) {
        this.buff = buff == null ? null : buff.trim();
    }
}