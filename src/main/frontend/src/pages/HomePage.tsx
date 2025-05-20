import React, { useState, useEffect } from 'react';
import axios from 'axios';
import {
    Box,
    Typography,
    List,
    ListItem,
    ListItemText,
    Button,
    Paper,
    Divider,
    CircularProgress,
    Alert,
    Card,
    CardContent
} from '@mui/material';
import RestaurantMenuIcon from '@mui/icons-material/RestaurantMenu';
import HomeIcon from '@mui/icons-material/Home';

interface Category {
    id: number;
    name: string;
}

interface Food {
    id: number;
    name: string;
    price: number;
}

const HomePage: React.FC = () => {
    const [categories, setCategories] = useState<Category[]>([]);
    const [selectedCategoryId, setSelectedCategoryId] = useState<number | null>(null);
    const [foods, setFoods] = useState<Food[]>([]);
    const [error, setError] = useState<string | null>(null);
    const [loading, setLoading] = useState<boolean>(false);

    useEffect(() => {
        axios
            .get<Category[]>('http://localhost:8080/category')
            .then((res) => setCategories(res.data))
            .catch((err) => {
                console.error(err);
                setError('Ошибка при загрузке категорий');
            });
    }, []);

    const loadFoods = (categoryId: number, categoryName: string) => {
        if (selectedCategoryId === categoryId) {
            setSelectedCategoryId(null);
            setFoods([]);
            setError(null);
            return;
        }

        setSelectedCategoryId(categoryId);
        setLoading(true);
        setFoods([]);
        setError(null);

        axios
            .get<Food[]>('http://localhost:8080/food/search-by-category', {
                params: { category: categoryName }
            })
            .then((res) => {
                setFoods(res.data);
                setLoading(false);
            })
            .catch((err) => {
                console.error(err);
                setError('Ошибка при загрузке блюд');
                setLoading(false);
            });
    };

    return (
        <Box sx={{ padding: '2rem', maxWidth: '1200px', marginLeft: 'auto', marginRight: 'auto' }}>
            <Card sx={{ mb: 4, width: '100%' }}>
                <CardContent>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                        <HomeIcon color="primary" sx={{ fontSize: 40 }} />
                        <Typography variant="h4" component="h1" gutterBottom>
                            Домашняя страница
                        </Typography>
                    </Box>
                </CardContent>
            </Card>

            <Card sx={{ width: '100%' }}>
                <CardContent sx={{ px: 3 }}>
                    <Box sx={{ display: 'flex', alignItems: 'stretch', gap: 2, mb: 2 }}>
                        <RestaurantMenuIcon color="primary" sx={{ fontSize: 40 }} />
                        <Typography variant="h5" component="h2">
                            Список категорий блюд
                        </Typography>
                    </Box>

                    {error && (
                        <Alert severity="error" sx={{ mb: 3 }}>
                            {error}
                        </Alert>
                    )}

                    <List sx={{ px: 0 }}>
                        {categories.map((category) => (
                            <React.Fragment key={category.id}>
                                <ListItem
                                    component="div"
                                    disableGutters
                                    sx={{
                                        display: 'flex',
                                        flexDirection: 'column',
                                        alignItems: 'flex-start',
                                        bgcolor: selectedCategoryId === category.id ? 'action.hover' : 'background.paper',
                                        px: 3,
                                        py: 2,
                                        mb: 2,
                                        boxShadow: 2,
                                        transition: 'box-shadow 0.3s',
                                        '&:hover': { boxShadow: 4 },
                                        width: '100%',
                                    }}
                                >
                                    <Button
                                        variant="text"
                                        onClick={() => loadFoods(category.id, category.name)}
                                        sx={{
                                            textAlign: 'left',
                                            textTransform: 'none',
                                            justifyContent: 'flex-start',
                                            width: '100%',
                                            p: 0,
                                        }}
                                    >
                                        <ListItemText
                                            primary={category.name}
                                            primaryTypographyProps={{ variant: 'h6' }}
                                        />
                                    </Button>

                                    {selectedCategoryId === category.id && (
                                        <Box sx={{ width: '100%', pl: 0, pr: 2, pt: 2 }}>
                                            {loading ? (
                                                <Box sx={{ display: 'flex', justifyContent: 'center', py: 3 }}>
                                                    <CircularProgress />
                                                </Box>
                                            ) : foods.length > 0 ? (
                                                <>
                                                    <Typography
                                                        variant="subtitle1"
                                                        gutterBottom
                                                        sx={{ fontWeight: 'bold', color: 'text.primary', ml: 0 }}
                                                    >
                                                        Блюда в этой категории:
                                                    </Typography>
                                                    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1, px: 2, pb: 1 }}>
                                                        {foods.map((food) => (
                                                            <Paper
                                                                key={food.id}
                                                                elevation={2}
                                                                sx={{
                                                                    p: 2,
                                                                    borderRadius: 2,
                                                                    backgroundColor: 'background.paper',
                                                                    transition: '0.3s',
                                                                    '&:hover': { boxShadow: 4 },
                                                                }}
                                                            >
                                                                <Typography variant="body1" sx={{ fontWeight: 'medium' }}>
                                                                    {food.name}
                                                                </Typography>
                                                                <Typography variant="body2" color="text.secondary">
                                                                    {food.price} ₽
                                                                </Typography>
                                                            </Paper>
                                                        ))}
                                                    </Box>
                                                </>
                                            ) : (
                                                <Typography variant="body2" color="text.secondary">
                                                    В этой категории пока нет блюд.
                                                </Typography>
                                            )}
                                        </Box>
                                    )}
                                </ListItem>
                                <Divider />
                            </React.Fragment>
                        ))}
                    </List>
                </CardContent>
            </Card>
        </Box>
    );
};

export default HomePage;
