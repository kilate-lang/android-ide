package mo.kilate.ide.os;

public interface Permission {
  void request();

  PermissionStatus check();
}
