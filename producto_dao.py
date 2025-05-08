import psycopg2
import tkinter as tk
from tkinter import messagebox
from tkinter import ttk

# ----- MODELO -----

class Producto:
    def __init__(self, codigo, nombre, descripcion, precio, categoria, stock):
        self.codigo = codigo
        self.nombre = nombre
        self.descripcion = descripcion
        self.precio = precio
        self.categoria = categoria
        self.stock = stock

    def __str__(self):
        return f"{self.codigo} - {self.nombre} - {self.precio} - {self.stock}"


class ProductoDAO:
    URL = "dbname='inventario' user='postgres' password='nuevaclave123' host='localhost' port='5433'"

    def insertar_producto(self, producto):
        try:
            with psycopg2.connect(self.URL) as conn:
                with conn.cursor() as cur:
                    cur.execute("CALL registrar_producto(%s, %s, %s, %s, %s, %s)", 
                                (producto.codigo, producto.nombre, producto.descripcion, producto.precio, producto.categoria, producto.stock))
            return True
        except psycopg2.Error as e:
            print(f"Error al insertar producto: {e}")
            return False

    def actualizar_stock(self, codigo, nuevo_stock, motivo, usuario_id):
        try:
            if nuevo_stock < 0:
                raise ValueError("El stock no puede ser negativo.")
            with psycopg2.connect(self.URL) as conn:
                with conn.cursor() as cur:
                    cur.execute("CALL actualizar_stock(%s, %s)", (codigo, nuevo_stock))
                    cur.execute("CALL registrar_movimiento_stock(%s, %s, %s, %s)", (codigo, nuevo_stock, motivo, usuario_id))
            return True
        except psycopg2.Error as e:
            print(f"Error al actualizar stock: {e}")
            return False
        except ValueError as ve:
            print(ve)
            return False

    def listar_productos(self):
        productos = []
        try:
            with psycopg2.connect(self.URL) as conn:
                with conn.cursor() as cur:
                    cur.execute("""
                        SELECT p.codigo, p.nombre, p.descripcion, p.precio, p.stock, c.nombre as categoria
                        FROM productos p JOIN categorias c ON p.categoria_id = c.id
                    """)
                    for row in cur.fetchall():
                        productos.append(Producto(row[0], row[1], row[2], row[3], row[5], row[4]))
        except psycopg2.Error as e:
            print(f"Error al listar productos: {e}")
        return productos

    def dar_de_baja_producto(self, codigo):
        try:
            with psycopg2.connect(self.URL) as conn:
                with conn.cursor() as cur:
                    cur.execute("CALL dar_de_baja_producto(%s)", (codigo,))
            return True
        except psycopg2.Error as e:
            print(f"Error al dar de baja producto: {e}")
            return False

# ----- APLICACIÓN -----

class InventarioApp:
    def __init__(self, root):
        self.root = root
        self.dao = ProductoDAO()
        self.usuario_id = None
        self.root.title("Sistema de Inventario")
        self.root.geometry("500x400")
        self.root.resizable(False, False)
        self.root.configure(bg="#f7f9fc")
        self.style = ttk.Style()
        self.style.theme_use('clam')
        self.style.configure("TFrame", background="#f7f9fc")
        self.style.configure("TLabel", background="#f7f9fc", font=("Segoe UI", 11))
        self.style.configure("TButton", padding=6, font=("Segoe UI", 10), background="#4a90e2", foreground="white")
        self.build_login_ui()

    def clear_window(self):
        for widget in self.root.winfo_children():
            widget.destroy()

    def build_login_ui(self):
        self.clear_window()
        frame = ttk.Frame(self.root, padding="30 30 30 30")
        frame.pack(expand=True)

        ttk.Label(frame, text="Correo:").grid(row=0, column=0, pady=10, sticky="w")
        self.correo_entry = ttk.Entry(frame, width=30)
        self.correo_entry.grid(row=0, column=1, pady=10)

        ttk.Label(frame, text="Contraseña:").grid(row=1, column=0, pady=10, sticky="w")
        self.clave_entry = ttk.Entry(frame, show="*", width=30)
        self.clave_entry.grid(row=1, column=1, pady=10)

        ttk.Button(frame, text="Iniciar sesión", command=self.verificar_credenciales).grid(row=2, column=0, columnspan=2, pady=20)

    def verificar_credenciales(self):
        correo = self.correo_entry.get().strip()
        clave = self.clave_entry.get().strip()

        if not correo or not clave:
            messagebox.showerror("Error", "Todos los campos son obligatorios")
            return

        try:
            conn = psycopg2.connect(ProductoDAO.URL)
            with conn.cursor() as cur:
                cur.execute("SELECT id, nombre FROM usuarios WHERE correo = %s AND contraseña = %s", (correo, clave))
                user = cur.fetchone()
                if user:
                    self.usuario_id = user[0]
                    self.build_menu()
                else:
                    messagebox.showerror("Error", "Credenciales incorrectas")
        except psycopg2.Error as e:
            messagebox.showerror("Error", f"Error de conexión: {e}")

    def build_menu(self):
        self.clear_window()
        frame = ttk.Frame(self.root, padding="30")
        frame.pack(expand=True)

        ttk.Label(frame, text="Menú Principal", font=("Segoe UI", 16, "bold")).pack(pady=10)
        for texto, comando in [
            ("Registrar producto", self.build_registro_producto),
            ("Actualizar stock", self.build_actualizar_stock),
            ("Listar productos", self.build_listar_productos),
            ("Dar de baja producto", self.build_dar_de_baja),
            ("Salir", self.root.quit)
        ]:
            ttk.Button(frame, text=texto, width=30, command=comando).pack(pady=5)

    def build_registro_producto(self):
        self.clear_window()
        frame = ttk.Frame(self.root, padding="30")
        frame.pack(expand=True)

        ttk.Label(frame, text="Registrar Producto", font=("Segoe UI", 16, "bold")).grid(row=0, column=0, columnspan=2, pady=10)

        self.codigo_entry = self._crear_campo(frame, "Código", 1)
        self.nombre_entry = self._crear_campo(frame, "Nombre", 2)
        self.descripcion_entry = self._crear_campo(frame, "Descripción", 3)
        self.precio_entry = self._crear_campo(frame, "Precio", 4)
        self.categoria_entry = self._crear_campo(frame, "Categoría ID", 5)
        self.stock_entry = self._crear_campo(frame, "Stock", 6)

        ttk.Button(frame, text="Registrar", command=self.registrar_producto).grid(row=7, column=0, columnspan=2, pady=10)
        ttk.Button(frame, text="Volver", command=self.build_menu).grid(row=8, column=0, columnspan=2)

    def registrar_producto(self):
        try:
            codigo = self.codigo_entry.get().strip()
            nombre = self.nombre_entry.get().strip()
            descripcion = self.descripcion_entry.get().strip()
            precio = self.precio_entry.get().strip()
            categoria = self.categoria_entry.get().strip()
            stock = self.stock_entry.get().strip()

            if not codigo or not nombre or not descripcion or not precio or not categoria or not stock:
                messagebox.showerror("Error", "Todos los campos son obligatorios")
                return

            producto = Producto(
                codigo,
                nombre,
                descripcion,
                float(precio),
                int(categoria),
                int(stock)
            )
            if self.dao.insertar_producto(producto):
                messagebox.showinfo("Éxito", "Producto registrado")
                self.build_menu()
            else:
                messagebox.showerror("Error", "No se pudo registrar")
        except ValueError:
            messagebox.showerror("Error", "Datos inválidos. Verifique los campos.")
        except Exception as e:
            messagebox.showerror("Error", str(e))

    def build_actualizar_stock(self):
        self.clear_window()
        frame = ttk.Frame(self.root, padding="30")
        frame.pack(expand=True)

        ttk.Label(frame, text="Actualizar Stock", font=("Segoe UI", 16, "bold")).grid(row=0, column=0, columnspan=2, pady=10)

        self.codigo_stock = self._crear_campo(frame, "Código producto", 1)
        self.nuevo_stock = self._crear_campo(frame, "Nuevo stock", 2)
        self.motivo = self._crear_campo(frame, "Motivo", 3)

        ttk.Button(frame, text="Actualizar", command=self.actualizar_stock).grid(row=4, column=0, columnspan=2, pady=10)
        ttk.Button(frame, text="Volver", command=self.build_menu).grid(row=5, column=0, columnspan=2)

    def actualizar_stock(self):
        try:
            codigo = self.codigo_stock.get().strip()
            nuevo_stock = self.nuevo_stock.get().strip()
            motivo = self.motivo.get().strip()

            if not codigo or not nuevo_stock or not motivo:
                messagebox.showerror("Error", "Todos los campos son obligatorios")
                return

            resultado = self.dao.actualizar_stock(
                codigo, int(nuevo_stock), motivo, self.usuario_id
            )
            if resultado:
                messagebox.showinfo("Éxito", "Stock actualizado")
                self.build_menu()
            else:
                messagebox.showerror("Error", "No se pudo actualizar")
        except ValueError:
            messagebox.showerror("Error", "El stock debe ser un número válido.")
        except Exception as e:
            messagebox.showerror("Error", str(e))

    def build_listar_productos(self):
        self.clear_window()
        frame = ttk.Frame(self.root, padding="20")
        frame.pack(expand=True)

        ttk.Label(frame, text="Productos", font=("Segoe UI", 16, "bold")).pack(pady=10)
        productos = self.dao.listar_productos()
        if not productos:
            ttk.Label(frame, text="No hay productos registrados.", font=("Segoe UI", 10)).pack(anchor="w", padx=10)
        else:
            for p in productos:
                ttk.Label(frame, text=str(p), font=("Segoe UI", 10)).pack(anchor="w", padx=10)
        ttk.Button(frame, text="Volver", command=self.build_menu).pack(pady=10)

    def build_dar_de_baja(self):
        self.clear_window()
        frame = ttk.Frame(self.root, padding="30")
        frame.pack(expand=True)

        ttk.Label(frame, text="Dar de baja producto", font=("Segoe UI", 16, "bold")).grid(row=0, column=0, columnspan=2, pady=10)

        self.codigo_baja = self._crear_campo(frame, "Código producto", 1)
        ttk.Button(frame, text="Dar de baja", command=self.dar_de_baja_producto).grid(row=2, column=0, columnspan=2, pady=10)
        ttk.Button(frame, text="Volver", command=self.build_menu).grid(row=3, column=0, columnspan=2)

    def dar_de_baja_producto(self):
        try:
            codigo = self.codigo_baja.get().strip()
            if not codigo:
                messagebox.showerror("Error", "El código del producto es obligatorio")
                return

            resultado = self.dao.dar_de_baja_producto(codigo)
            if resultado:
                messagebox.showinfo("Éxito", "Producto dado de baja")
            else:
                messagebox.showerror("Error", "Producto no encontrado")
        except Exception as e:
            messagebox.showerror("Error", str(e))

    def _crear_campo(self, frame, texto, fila):
        ttk.Label(frame, text=texto + ":").grid(row=fila, column=0, pady=5, sticky="w")
        entry = ttk.Entry(frame, width=30)
        entry.grid(row=fila, column=1, pady=5)
        return entry


if __name__ == "__main__":
    root = tk.Tk()
    app = InventarioApp(root)
    root.mainloop()
