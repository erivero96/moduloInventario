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
        except Exception:
            return False

    def actualizar_stock(self, codigo, nuevo_stock, motivo, usuario_id):
        try:
            with psycopg2.connect(self.URL) as conn:
                with conn.cursor() as cur:
                    cur.execute("CALL actualizar_stock(%s, %s)", (codigo, nuevo_stock))
                    cur.execute("CALL registrar_movimiento_stock(%s, %s, %s, %s)", (codigo, nuevo_stock, motivo, usuario_id))
            return True
        except Exception:
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
        except Exception:
            pass
        return productos

    def dar_de_baja_producto(self, codigo):
        try:
            with psycopg2.connect(self.URL) as conn:
                with conn.cursor() as cur:
                    cur.execute("CALL dar_de_baja_producto(%s)", (codigo,))
            return True
        except Exception:
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
        correo = self.correo_entry.get()
        clave = self.clave_entry.get()

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
        except Exception as e:
            messagebox.showerror("Error", str(e))

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
            producto = Producto(
                self.codigo_entry.get(),
                self.nombre_entry.get(),
                self.descripcion_entry.get(),
                float(self.precio_entry.get()),
                int(self.categoria_entry.get()),
                int(self.stock_entry.get())
            )
            if self.dao.insertar_producto(producto):
                messagebox.showinfo("Éxito", "Producto registrado")
                self.build_menu()
            else:
                messagebox.showerror("Error", "No se pudo registrar")
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
            resultado = self.dao.actualizar_stock(
                self.codigo_stock.get(), int(self.nuevo_stock.get()), self.motivo.get(), self.usuario_id
            )
            if resultado:
                messagebox.showinfo("Éxito", "Stock actualizado")
                self.build_menu()
            else:
                messagebox.showerror("Error", "No se pudo actualizar")
        except Exception as e:
            messagebox.showerror("Error", str(e))

    def build_listar_productos(self):
        self.clear_window()
        frame = ttk.Frame(self.root, padding="20")
        frame.pack(expand=True)

        ttk.Label(frame, text="Productos", font=("Segoe UI", 16, "bold")).pack(pady=10)
        productos = self.dao.listar_productos()
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
            resultado = self.dao.dar_de_baja_producto(self.codigo_baja.get())
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
