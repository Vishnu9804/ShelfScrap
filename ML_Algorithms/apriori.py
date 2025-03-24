import pandas as pd
import json
from mlxtend.frequent_patterns import apriori, association_rules
from mlxtend.preprocessing import TransactionEncoder

# File paths
excel_file = "AlreadyReadBooks.xlsx"
json_file_1 = "recommendations.json"
json_file_2 = r"C:\My Corner\Java Dev\Ink-Paper-BookCatalogue\frontend\public\recommendations.json"

# Load the Excel file
df = pd.read_excel(excel_file, header=None)

# Extract usernames and book transactions
user_books = {}
transactions = []

for i in range(len(df)):
    username = df.iloc[i, 0]  # First column is username
    books = df.iloc[i, 1:].dropna().astype(str).tolist()  # Convert book IDs to strings
    
    if books:
        transactions.append(books)  # Store books list
        user_books[username] = set(books)  # Store books read by each user

# Encode transactions into binary format
te = TransactionEncoder()
te_ary = te.fit(transactions).transform(transactions)
df_encoded = pd.DataFrame(te_ary, columns=te.columns_)

# Apply Apriori algorithm
frequent_itemsets = apriori(df_encoded, min_support=0.02, use_colnames=True)  # Adjusted min_support
rules = association_rules(frequent_itemsets, metric="lift", min_threshold=1.0)

# Generate recommendations
recommendations_dict = {}  # Dictionary to store unique recommendations per user

for _, row in rules.iterrows():
    antecedent_books = set(row['antecedents'])  # Books already read
    consequent_books = set(row['consequents'])  # Books to recommend

    for username, user_read_books in user_books.items():
        if antecedent_books.issubset(user_read_books):  # If user has read all antecedents
            new_recommendations = consequent_books - user_read_books  # Remove already read books

            for book in new_recommendations:
                book_id = str(int(float(book)))  # Convert to clean book ID (remove .0 issue)

                if username not in recommendations_dict:
                    recommendations_dict[username] = set()
                
                recommendations_dict[username].add(book_id)  # Ensure unique recommendations

# Convert to list format
recommendations = [
    {"username": user, "recommended_book": book}
    for user, books in recommendations_dict.items()
    for book in books
]

# Save recommendations to both paths
with open(json_file_1, "w") as json_output_1:
    json.dump(recommendations, json_output_1, indent=4)

with open(json_file_2, "w") as json_output_2:
    json.dump(recommendations, json_output_2, indent=4)

print(f"Recommendations saved to:\n1. {json_file_1}\n2. {json_file_2}")
