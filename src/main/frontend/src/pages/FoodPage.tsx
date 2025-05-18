import React, { useEffect, useState } from 'react';
import axios from 'axios';
import {
    Box,
    Typography,
    Card,
    CardContent,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Paper,
    IconButton,
    Button,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    TextField,
    Snackbar,
    MenuItem,
    Select,
    CircularProgress,
    Alert,
    InputLabel,
    FormControl,
    Checkbox,
    FormControlLabel,
} from '@mui/material';
import {
    Edit as EditIcon,
    Delete as DeleteIcon,
    Restaurant as RestaurantIcon,
    Add as AddIcon,
} from '@mui/icons-material';

interface Category {
    id: number;
    name: string;
}

interface Ingredient {
    id: number;
    name: string;
}

interface Food {
    id: number;
    name: string;
    price: number;
    category: Category;
    ingredients: Ingredient[];
}

const FoodPage: React.FC = () => {
    const [foods, setFoods] = useState<Food[]>([]);
    const [categories, setCategories] = useState<Category[]>([]);
    const [ingredients, setIngredients] = useState<Ingredient[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [dialogOpen, setDialogOpen] = useState(false);
    const [snackbarMessage, setSnackbarMessage] = useState('');
    const [snackbarOpen, setSnackbarOpen] = useState(false);
    const [isEditing, setIsEditing] = useState(false);

    const [currentFood, setCurrentFood] = useState<{
        id?: number;
        name: string;
        price: number;
        categoryId?: number;
        ingredients: Ingredient[];
    }>({
        name: '',
        price: 0,
        categoryId: undefined,
        ingredients: [],
    });

    // Для диалога управления ингредиентами
    const [ingredientDialogOpen, setIngredientDialogOpen] = useState(false);
    const [selectedFoodForIngredients, setSelectedFoodForIngredients] = useState<Food | null>(null);
    const [selectedIngredientIds, setSelectedIngredientIds] = useState<number[]>([]);

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {
        setLoading(true);
        try {
            const [foodRes, categoryRes, ingredientRes] = await Promise.all([
                axios.get<Food[]>('http://localhost:8080/food'),
                axios.get<Category[]>('http://localhost:8080/category'),
                axios.get<Ingredient[]>('http://localhost:8080/ingredient'),
            ]);
            setFoods(foodRes.data);
            setCategories(categoryRes.data);
            setIngredients(ingredientRes.data);
            setError(null);
        } catch {
            setError('Ошибка при загрузке данных');
        } finally {
            setLoading(false);
        }
    };

    const handleOpenDialog = (food: Food | null) => {
        setIsEditing(!!food);
        setCurrentFood(
            food
                ? {
                    id: food.id,
                    name: food.name,
                    price: food.price,
                    categoryId: food.category?.id,
                    ingredients: food.ingredients || [],
                }
                : {
                    name: '',
                    price: 0,
                    categoryId: undefined,
                    ingredients: [],
                }
        );
        setDialogOpen(true);
    };

    const handleCloseDialog = () => {
        setDialogOpen(false);
        setCurrentFood({ name: '', price: 0, categoryId: undefined, ingredients: [] });
    };

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setCurrentFood((prev) => ({
            ...prev,
            [name]: name === 'price' ? parseFloat(value) : value,
        }));
    };

    const handleCategoryChange = (e: any) => {
        setCurrentFood((prev) => ({
            ...prev,
            categoryId: parseInt(e.target.value),
        }));
    };

    const handleSave = async () => {
        try {
            const foodData = {
                name: currentFood.name,
                price: currentFood.price,
                category: { id: currentFood.categoryId },
                ingredients: [], // пустой массив, чтобы не передавать ингредиенты
            };

            if (isEditing && currentFood.id) {
                await axios.put(`http://localhost:8080/food/${currentFood.id}`, foodData);
                setSnackbarMessage('Блюдо обновлено');
            } else {
                await axios.post('http://localhost:8080/food', foodData);
                setSnackbarMessage('Блюдо добавлено');
            }

            await new Promise((r) => setTimeout(r, 100));
            handleCloseDialog();
            setSnackbarOpen(true);
            fetchData();
        } catch {
            setError('Ошибка при сохранении блюда');
        }
    };

    const handleDelete = async (id: number) => {
        try {
            await axios.delete(`http://localhost:8080/food/${id}`);
            setSnackbarMessage('Блюдо удалено');
            setSnackbarOpen(true);
            fetchData();
        } catch {
            setError('Ошибка при удалении');
        }
    };

    // --- Управление ингредиентами для конкретного блюда ---

    const handleOpenIngredientDialog = (food: Food) => {
        setSelectedFoodForIngredients(food);
        setSelectedIngredientIds(food.ingredients?.map((i) => i.id) || []);
        setIngredientDialogOpen(true);
    };

    const handleCloseIngredientDialog = () => {
        setIngredientDialogOpen(false);
        setSelectedFoodForIngredients(null);
        setSelectedIngredientIds([]);
    };

    const handleIngredientToggle = (id: number) => {
        setSelectedIngredientIds((prev) =>
            prev.includes(id) ? prev.filter((x) => x !== id) : [...prev, id]
        );
    };

    const handleSaveIngredients = async () => {
        if (!selectedFoodForIngredients) return;

        try {
            // Можно добавить очистку текущих ингредиентов если нужно
            // await axios.delete(`http://localhost:8080/food/${selectedFoodForIngredients.id}/ingredients`);

            // Добавляем ингредиенты по одному POST запросу
            for (const ingredientId of selectedIngredientIds) {
                await axios.post(
                    `http://localhost:8080/food/${selectedFoodForIngredients.id}/ingredient/${ingredientId}`
                );
            }

            setSnackbarMessage('Ингредиенты обновлены');
            setSnackbarOpen(true);
            handleCloseIngredientDialog();
            fetchData();
        } catch {
            setError('Ошибка при обновлении ингредиентов');
        }
    };

    return (
        <Box sx={{ p: 3, maxWidth: 1200, margin: '0 auto' }}>
            <Card sx={{ mb: 4 }}>
                <CardContent>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                        <RestaurantIcon color="primary" sx={{ fontSize: 40 }} />
                        <Typography variant="h4">Управление блюдами</Typography>
                    </Box>
                </CardContent>
            </Card>

            <Box sx={{ display: 'flex', justifyContent: 'flex-end', mb: 2 }}>
                <Button variant="contained" startIcon={<AddIcon />} onClick={() => handleOpenDialog(null)}>
                    Добавить блюдо
                </Button>
            </Box>

            {loading ? (
                <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
                    <CircularProgress />
                </Box>
            ) : error ? (
                <Alert severity="error">{error}</Alert>
            ) : (
                <TableContainer component={Paper}>
                    <Table>
                        <TableHead>
                            <TableRow sx={{ backgroundColor: '#f5f5f5' }}>
                                <TableCell>Название</TableCell>
                                <TableCell>Цена</TableCell>
                                <TableCell>Категория</TableCell>
                                <TableCell>Ингредиенты</TableCell>
                                <TableCell>Действия</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {foods.map((food) => (
                                <TableRow key={food.id}>
                                    <TableCell>{food.name}</TableCell>
                                    <TableCell>{food.price} ₽</TableCell>
                                    <TableCell>{food.category?.name || '—'}</TableCell>
                                    <TableCell>{food.ingredients?.map((i) => i.name).join(', ') || '—'}</TableCell>
                                    <TableCell>
                                        <IconButton onClick={() => handleOpenDialog(food)} color="primary">
                                            <EditIcon />
                                        </IconButton>
                                        <IconButton onClick={() => handleDelete(food.id)} color="error">
                                            <DeleteIcon />
                                        </IconButton>
                                        <Button
                                            variant="outlined"
                                            size="small"
                                            onClick={() => handleOpenIngredientDialog(food)}
                                            sx={{ ml: 1 }}
                                        >
                                            Добавить Ингредиенты
                                        </Button>
                                    </TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </TableContainer>
            )}

            {/* Диалог добавления/редактирования блюда */}
            <Dialog open={dialogOpen} onClose={handleCloseDialog} maxWidth="sm" fullWidth>
                <DialogTitle>{isEditing ? 'Редактировать блюдо' : 'Добавить новое блюдо'}</DialogTitle>
                <DialogContent sx={{ pt: 2 }}>
                    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                        <TextField
                            label="Название"
                            name="name"
                            value={currentFood.name}
                            onChange={handleInputChange}
                            fullWidth
                        />
                        <TextField
                            label="Цена"
                            name="price"
                            type="number"
                            value={currentFood.price}
                            onChange={handleInputChange}
                            fullWidth
                        />
                        <FormControl fullWidth>
                            <InputLabel>Категория</InputLabel>
                            <Select value={currentFood.categoryId?.toString() || ''} onChange={handleCategoryChange} fullWidth>
                                {categories.map((cat) => (
                                    <MenuItem key={cat.id} value={cat.id}>
                                        {cat.name}
                                    </MenuItem>
                                ))}
                            </Select>
                        </FormControl>
                    </Box>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleCloseDialog}>Отмена</Button>
                    <Button
                        onClick={handleSave}
                        variant="contained"
                        color="primary"
                        disabled={!currentFood.name || !currentFood.price || !currentFood.categoryId}
                    >
                        Сохранить
                    </Button>
                </DialogActions>
            </Dialog>

            {/* Диалог управления ингредиентами */}
            <Dialog open={ingredientDialogOpen} onClose={handleCloseIngredientDialog} maxWidth="sm" fullWidth>
                <DialogTitle>Управление ингредиентами для блюда</DialogTitle>
                <DialogContent>
                    <Typography variant="subtitle1" gutterBottom>
                        {selectedFoodForIngredients?.name}
                    </Typography>
                    <Box sx={{ maxHeight: 300, overflowY: 'auto' }}>
                        {ingredients.map((ing) => (
                            <FormControlLabel
                                key={ing.id}
                                control={
                                    <Checkbox
                                        checked={selectedIngredientIds.includes(ing.id)}
                                        onChange={() => handleIngredientToggle(ing.id)}
                                    />
                                }
                                label={ing.name}
                            />
                        ))}
                    </Box>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleCloseIngredientDialog}>Отмена</Button>
                    <Button variant="contained" onClick={handleSaveIngredients} disabled={selectedIngredientIds.length === 0}>
                        Сохранить
                    </Button>
                </DialogActions>
            </Dialog>

            <Snackbar
                open={snackbarOpen}
                autoHideDuration={3000}
                onClose={() => setSnackbarOpen(false)}
                message={snackbarMessage}
                anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
            />
        </Box>
    );
};

export default FoodPage;
