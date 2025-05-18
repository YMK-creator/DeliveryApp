import React, { useEffect, useState } from 'react';
import axios from 'axios';
import {
    Box,
    Typography,
    Card,
    CardContent,
    TextField,
    Button,
    List,
    ListItem,
    ListItemText,
    IconButton,
    Divider,
    Alert,
    CircularProgress,
} from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';
import EditIcon from '@mui/icons-material/Edit';
import SaveIcon from '@mui/icons-material/Save';
import AddCircleIcon from '@mui/icons-material/AddCircle';
import CategoryIcon from '@mui/icons-material/Category';

interface Category {
    id: number;
    name: string;
}

const CategoryPage: React.FC = () => {
    const [categories, setCategories] = useState<Category[]>([]);
    const [loading, setLoading] = useState<boolean>(false);
    const [error, setError] = useState<string | null>(null);
    const [newCategoryName, setNewCategoryName] = useState<string>('');
    const [editingId, setEditingId] = useState<number | null>(null);
    const [editedName, setEditedName] = useState<string>('');

    const fetchCategories = () => {
        setLoading(true);
        axios
            .get<Category[]>('http://localhost:8080/category')
            .then((res) => {
                setCategories(res.data);
                setError(null);
                setLoading(false);
            })
            .catch((err) => {
                console.error(err);
                setError('Не удалось загрузить категории');
                setLoading(false);
            });
    };

    useEffect(() => {
        fetchCategories();
    }, []);

    const handleDelete = (id: number) => {
        axios
            .delete(`http://localhost:8080/category/${id}`)
            .then(() => {
                setCategories((prev) => prev.filter((cat) => cat.id !== id));
            })
            .catch((err) => {
                console.error(err);
                setError('Ошибка при удалении категории');
            });
    };

    const handleEdit = (category: Category) => {
        setEditingId(category.id);
        setEditedName(category.name);
    };

    const handleSave = (id: number) => {
        axios
            .put(`http://localhost:8080/category/${id}`, { id, name: editedName })
            .then(() => {
                setCategories((prev) =>
                    prev.map((cat) =>
                        cat.id === id ? { ...cat, name: editedName } : cat
                    )
                );
                setEditingId(null);
                setEditedName('');
            })
            .catch((err) => {
                console.error(err);
                setError('Ошибка при обновлении категории');
            });
    };

    const handleAddCategory = () => {
        if (!newCategoryName.trim()) return;

        axios
            .post('http://localhost:8080/category', { name: newCategoryName })
            .then((res) => {
                setCategories((prev) => [...prev, res.data]);
                setNewCategoryName('');
            })
            .catch((err) => {
                console.error(err);
                setError('Ошибка при добавлении категории');
            });
    };

    return (
        <Box sx={{ padding: '2rem', maxWidth: '800px' }}>
            <Card sx={{ mb: 4 }}>
                <CardContent>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                        <CategoryIcon color="primary" sx={{ fontSize: 40 }} />
                        <Typography variant="h4" component="h1" gutterBottom>
                            Категории блюд
                        </Typography>
                    </Box>
                </CardContent>
            </Card>

            {error && (
                <Alert severity="error" sx={{ mb: 2 }}>
                    {error}
                </Alert>
            )}

            <Box sx={{ mb: 3, display: 'flex', gap: 2 }}>
                <TextField
                    label="Новая категория"
                    value={newCategoryName}
                    onChange={(e) => setNewCategoryName(e.target.value)}
                    fullWidth
                />
                <Button
                    variant="contained"
                    startIcon={<AddCircleIcon />}
                    onClick={handleAddCategory}
                >
                    Добавить
                </Button>
            </Box>

            {loading ? (
                <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
                    <CircularProgress />
                </Box>
            ) : (
                <List>
                    {categories.map((category) => (
                        <React.Fragment key={category.id}>
                            <ListItem
                                secondaryAction={
                                    <>
                                        {editingId === category.id ? (
                                            <IconButton
                                                edge="end"
                                                onClick={() => handleSave(category.id)}
                                            >
                                                <SaveIcon />
                                            </IconButton>
                                        ) : (
                                            <IconButton
                                                edge="end"
                                                onClick={() => handleEdit(category)}
                                            >
                                                <EditIcon />
                                            </IconButton>
                                        )}
                                        <IconButton
                                            edge="end"
                                            onClick={() => handleDelete(category.id)}
                                        >
                                            <DeleteIcon />
                                        </IconButton>
                                    </>
                                }
                            >
                                {editingId === category.id ? (
                                    <TextField
                                        value={editedName}
                                        onChange={(e) => setEditedName(e.target.value)}
                                        fullWidth
                                    />
                                ) : (
                                    <ListItemText primary={category.name} />
                                )}
                            </ListItem>
                            <Divider />
                        </React.Fragment>
                    ))}
                </List>
            )}
        </Box>
    );
};

export default CategoryPage;
