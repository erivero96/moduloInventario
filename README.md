INSTRUCCIONES PARA IMPLEMENTAR EL SISTEMA DE INVENTARIO EN PYTHON
Para comenzar, primero se debe crear la base de datos en PostgreSQL. Esto se realiza abriendo una
terminal de PostgreSQL (por ejemplo, utilizando el comando psql) y ejecutando las instrucciones necesarias
para crear la base de datos llamada 'inventario'. Dentro de esta base de datos se deben crear las tablas
'usuarios', 'categorias', 'productos' y 'movimientos_stock', así como los procedimientos almacenados
correspondientes que permiten registrar productos, actualizar stock, registrar movimientos y dar de baja
productos. También se debe insertar al menos un usuario en la tabla 'usuarios' para poder iniciar sesión en
el sistema. La contraseña por defecto del usuario de prueba puede ser 'admin' y el correo
'admin@inventario.com'.
A continuación, se debe configurar un entorno virtual en el proyecto para aislar las dependencias. Desde la
terminal, y ubicado en la carpeta raíz del proyecto, se debe ejecutar el siguiente comando:
python3 -m venv venv
Una vez creado el entorno virtual, debe activarse. Si se utiliza Linux o macOS, el comando es:
source venv/bin/activate
Si se utiliza Windows en modo consola (cmd), el comando es:
venv\Scripts\activate
En caso de usar PowerShell en Windows, el comando sería:
venv\Scripts\Activate.ps1
Con el entorno virtual activado, es necesario instalar los paquetes requeridos por la aplicación. El único
paquete adicional que debe instalarse es el conector de PostgreSQL para Python. Esto se hace ejecutando
el siguiente comando:
pip install psycopg2-binary
El módulo tkinter no requiere instalación adicional ya que forma parte de la biblioteca estándar de Python,
siempre que se haya instalado Python completo.
Finalmente, para ejecutar la aplicación, basta con ejecutar el archivo principal del sistema dentro del entorno
virtual activo. Si, por ejemplo, el archivo que contiene todo el código se llama 'inventario_app.py', se debe
ejecutar:
python inventario_app.py
Con esto, se inicializa la interfaz gráfica del sistema, empezando por la pantalla de inicio de sesión. Desde
allí, una vez autenticado el usuario, se accede al menú principal que da acceso a las funcionalidades del
sistema de inventario.
NOTA IMPORTANTE SOBRE CONEXIÓN A LA BASE DE DATOS
En el archivo que contiene la clase ProductoDAO, se debe prestar especial atención a la línea que define los
parámetros de conexión a PostgreSQL. Específicamente, la línea:
URL = "dbname='inventario' user='postgres' password='nuevaclave123' host='localhost' port='5433'"
debe ser modificada de acuerdo con los valores reales de la instancia de PostgreSQL del usuario. Es decir,
se deben reemplazar el nombre de la base de datos, el nombre de usuario, la contraseña, el host y el puerto
según corresponda a su configuración local. Esta modificación es necesaria para establecer una conexión
correcta con la base de datos.
