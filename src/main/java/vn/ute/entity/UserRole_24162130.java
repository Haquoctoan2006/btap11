package vn.ute.entity;

import jakarta.persistence.*;

/** Bang UserRoles trong database ShopOnline. */
@Entity(name = "UserRole")
@Table(name = "UserRoles")
public class UserRole_24162130 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "roleId")
    private Integer roleId;

    @Column(name = "roleName", length = 50)
    private String roleName;

    public UserRole_24162130() {
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
