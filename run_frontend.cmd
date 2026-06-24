@echo off
echo Starting Aegis Cyber Shield Frontend Server...
echo Open your web browser and navigate to:
echo http://localhost:5000/
echo.
cd frontend
python -m http.server 5000
