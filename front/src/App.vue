<script setup>
import { computed, onMounted, reactive, ref } from 'vue'

const authMode = ref('login')
const authPanelOpen = ref(false)
const activeView = ref('discover')
const authForm = reactive({
  email: '',
  password: '',
  displayName: '',
})
const bookSearch = reactive({
  q: 'java',
  selectedGenreIds: [],
  tags: 'programming',
})
const reviewForm = reactive({
  rating: 5,
  body: '',
})
const userLookupId = ref('')
const state = reactive({
  user: null,
  genres: [],
  searchResults: [],
  selectedBook: null,
  reviews: [],
  favorites: [],
  ranking: [],
  selectedGenreId: '',
  featuredUsers: [],
  selectedUser: null,
  loading: '',
  notice: '',
  error: '',
})

const isLoggedIn = computed(() => Boolean(state.user))
const selectedGenreIds = computed(() => new Set(bookSearch.selectedGenreIds.map(Number)))
const favoriteIds = computed(() => new Set(state.favorites.map((book) => book.id)))
const navItems = computed(() => [
  { id: 'discover', label: '探す' },
  { id: 'library', label: '読む' },
  { id: 'people', label: 'ユーザー' },
  ...(state.selectedBook ? [{ id: 'detail', label: '詳細' }] : []),
])
const canShowEmptyState = computed(() => !state.loading && !state.error)
const hasSearchResults = computed(() => state.searchResults.length > 0)
const hasFavorites = computed(() => state.favorites.length > 0)
const hasReviews = computed(() => state.reviews.length > 0)
const hasRanking = computed(() => state.ranking.length > 0)
const hasFeaturedUsers = computed(() => state.featuredUsers.length > 0)

async function api(path, options = {}) {
  const response = await fetch(path, {
    headers: {
      'Content-Type': 'application/json',
      ...(options.headers ?? {}),
    },
    ...options,
  })
  if (response.status === 204) {
    return null
  }
  const text = await response.text()
  const data = text ? JSON.parse(text) : null
  if (!response.ok) {
    const error = new Error(data?.error?.message ?? 'Request failed.')
    error.status = response.status
    throw error
  }
  return data
}

async function run(label, action, successMessage = '') {
  state.loading = label
  state.error = ''
  state.notice = ''
  try {
    const result = await action()
    if (successMessage) {
      state.notice = successMessage
    }
    return result
  } catch (error) {
    state.error = error.message
    return null
  } finally {
    state.loading = ''
  }
}

async function loadMe() {
  state.loading = 'me'
  try {
    state.user = await api('/api/auth/me')
  } catch (error) {
    state.user = null
    if (error.status !== 401) {
      state.error = error.message
    }
  } finally {
    state.loading = ''
  }
}

async function loadGenres() {
  state.genres = (await run('genres', () => api('/api/genres'))) ?? []
  if (!state.selectedGenreId && state.genres.length > 0) {
    state.selectedGenreId = String(state.genres[0].id)
  }
}

async function loadFeaturedUsers() {
  state.featuredUsers = (await run('featuredUsers', () => api('/api/users/featured'))) ?? []
}

async function submitAuth() {
  const endpoint = authMode.value === 'register' ? '/api/auth/register' : '/api/auth/login'
  const payload =
    authMode.value === 'register'
      ? {
          email: authForm.email,
          password: authForm.password,
          displayName: authForm.displayName,
        }
      : {
          email: authForm.email,
          password: authForm.password,
        }
  const result = await run(
    'auth',
    () =>
      api(endpoint, {
        method: 'POST',
        body: JSON.stringify(payload),
      }),
    authMode.value === 'register' ? '登録しました。' : 'ログインしました。',
  )
  if (result?.user) {
    state.user = result.user
    authPanelOpen.value = false
    authForm.password = ''
    await loadFavorites()
  }
}

async function logout() {
  await run(
    'logout',
    () =>
      api('/api/auth/logout', {
        method: 'POST',
      }),
    'ログアウトしました。',
  )
  state.user = null
  state.favorites = []
  authPanelOpen.value = true
}

async function searchBooks() {
  state.searchResults =
    (await run('search', () => api(`/api/books/search?q=${encodeURIComponent(bookSearch.q)}`))) ??
    []
}

function bookPayload(book) {
  const tagValues = bookSearch.tags
    .split(',')
    .map((tag) => tag.trim())
    .filter(Boolean)
  return {
    googleVolumeId: book.googleVolumeId,
    title: book.title,
    authors: book.authors ?? [],
    description: book.description,
    publisher: book.publisher,
    publishedDate: book.publishedDate,
    isbn10: book.isbn10,
    isbn13: book.isbn13,
    thumbnailUrl: book.thumbnailUrl,
    genreIds: Array.from(selectedGenreIds.value),
    tags: tagValues,
  }
}

async function saveBook(book) {
  const saved = await run(
    'saveBook',
    () =>
      api('/api/books', {
        method: 'POST',
        body: JSON.stringify(bookPayload(book)),
      }),
    '書籍を保存しました。',
  )
  if (saved) {
    await selectBook(saved)
  }
}

async function selectBook(book) {
  const detail = book.id ? await run('book', () => api(`/api/books/${book.id}`)) : null
  state.selectedBook = detail ?? book
  activeView.value = 'detail'
  await loadReviews()
  reviewForm.rating = 5
  reviewForm.body = ''
}

async function loadReviews() {
  if (!state.selectedBook?.id) {
    state.reviews = []
    return
  }
  state.reviews =
    (await run('reviews', () => api(`/api/books/${state.selectedBook.id}/reviews`))) ?? []
}

async function loadFavorites() {
  if (!state.user) {
    return
  }
  state.favorites = (await run('favorites', () => api('/api/me/favorites'))) ?? []
}

async function favoriteBook(book) {
  if (!book?.id) {
    state.error = '先に書籍を保存してください。'
    return
  }
  await run(
    'favorite',
    () =>
      api(`/api/books/${book.id}/favorite`, {
        method: 'POST',
      }),
    'お気に入りに追加しました。',
  )
  await loadFavorites()
}

async function unfavoriteBook(book) {
  if (!book?.id) {
    return
  }
  await run(
    'unfavorite',
    () =>
      api(`/api/books/${book.id}/favorite`, {
        method: 'DELETE',
      }),
    'お気に入りから削除しました。',
  )
  await loadFavorites()
}

async function submitReview() {
  if (!state.selectedBook?.id) {
    state.error = '先に保存済みの書籍を選択してください。'
    return
  }
  await run(
    'review',
    () =>
      api(`/api/books/${state.selectedBook.id}/review`, {
        method: 'PUT',
        body: JSON.stringify({
          rating: Number(reviewForm.rating),
          body: reviewForm.body,
        }),
      }),
    'レビューを保存しました。',
  )
  await loadReviews()
}

async function deleteReview() {
  if (!state.selectedBook?.id) {
    return
  }
  await run(
    'deleteReview',
    () =>
      api(`/api/books/${state.selectedBook.id}/review`, {
        method: 'DELETE',
      }),
    'レビューを削除しました。',
  )
  await loadReviews()
}

async function loadRanking() {
  if (!state.selectedGenreId) {
    return
  }
  state.ranking =
    (await run('ranking', () => api(`/api/genres/${state.selectedGenreId}/ranking`))) ?? []
}

async function loadUser(userId) {
  const id = userId || userLookupId.value
  if (!id) {
    return
  }
  state.selectedUser = await run('user', () => api(`/api/users/${id}`))
}

async function followUser(user) {
  await run(
    'follow',
    () =>
      api(`/api/users/${user.id}/follow`, {
        method: 'POST',
      }),
    'フォローしました。',
  )
  await loadFeaturedUsers()
  await loadUser(user.id)
}

async function unfollowUser(user) {
  await run(
    'unfollow',
    () =>
      api(`/api/users/${user.id}/follow`, {
        method: 'DELETE',
      }),
    'フォローを解除しました。',
  )
  await loadFeaturedUsers()
  await loadUser(user.id)
}

async function searchByTag(tag) {
  bookSearch.q = tag.name
  activeView.value = 'discover'
  await searchBooks()
}

function showAuth(mode = 'login') {
  authMode.value = mode
  authPanelOpen.value = true
}

function joinAuthors(book) {
  return book?.authors?.length ? book.authors.join(', ') : '著者未設定'
}

function hasFavorite(book) {
  return favoriteIds.value.has(book?.id)
}

function formatRating(value) {
  return Number(value || 0).toFixed(1)
}

onMounted(async () => {
  await Promise.all([loadMe(), loadGenres(), loadFeaturedUsers()])
  await Promise.all([searchBooks(), loadRanking(), loadFavorites()])
})
</script>

<template>
  <main class="app-shell">
    <header class="app-header">
      <div class="brand-block">
        <p class="eyebrow">Programmer Books</p>
        <h1>技術書を探して、読書ログを育てる。</h1>
        <p>
          検索、ランキング、お気に入り、レビューを一つの流れで扱えます。
        </p>
      </div>

      <div class="account-strip">
        <div>
          <p class="eyebrow">Account</p>
          <p v-if="state.user" class="session-name">{{ state.user.displayName }} でログイン中</p>
          <p v-else class="session-name">ログインすると記録を保存できます</p>
        </div>
        <div class="actions">
          <button v-if="state.user" type="button" class="secondary" @click="logout">
            ログアウト
          </button>
          <template v-else>
            <button type="button" @click="showAuth('login')">ログイン</button>
            <button type="button" class="secondary" @click="showAuth('register')">登録</button>
          </template>
        </div>
      </div>
    </header>

    <nav class="view-nav" aria-label="主要画面">
      <button
        v-for="item in navItems"
        :key="item.id"
        :class="{ active: activeView === item.id }"
        type="button"
        @click="activeView = item.id"
      >
        {{ item.label }}
      </button>
    </nav>

    <p v-if="state.loading" class="status">読み込み中...</p>
    <p v-if="state.notice" class="status success">{{ state.notice }}</p>
    <p v-if="state.error" class="status error">{{ state.error }}</p>

    <section v-if="authPanelOpen && !state.user" class="auth-panel">
      <div class="panel-heading row-heading">
        <div>
          <p class="eyebrow">Account</p>
          <h2>{{ authMode === 'register' ? 'アカウント登録' : 'ログイン' }}</h2>
        </div>
        <button type="button" class="secondary" @click="authPanelOpen = false">閉じる</button>
      </div>
      <div class="auth-layout">
        <div class="mode-switch">
          <button :class="{ active: authMode === 'login' }" type="button" @click="authMode = 'login'">
            ログイン
          </button>
          <button
            :class="{ active: authMode === 'register' }"
            type="button"
            @click="authMode = 'register'"
          >
            登録
          </button>
        </div>
        <form class="form" @submit.prevent="submitAuth">
          <label>
            メールアドレス
            <input v-model="authForm.email" type="email" autocomplete="email" required />
          </label>
          <label>
            パスワード
            <input
              v-model="authForm.password"
              type="password"
              autocomplete="current-password"
              minlength="8"
              required
            />
          </label>
          <label v-if="authMode === 'register'">
            表示名
            <input v-model="authForm.displayName" type="text" required />
          </label>
          <button type="submit">{{ authMode === 'register' ? '登録する' : 'ログインする' }}</button>
        </form>
      </div>
    </section>

    <section v-show="activeView === 'discover'" class="view-section">
      <div class="panel">
        <div class="panel-heading">
          <p class="eyebrow">Discovery</p>
          <h2>書籍検索</h2>
        </div>
        <form class="form" @submit.prevent="searchBooks">
          <label>
            キーワード
            <input v-model="bookSearch.q" type="search" required />
          </label>
          <label>
            保存時のタグ
            <input v-model="bookSearch.tags" type="text" placeholder="java, testing" />
          </label>
          <fieldset>
            <legend>保存時のジャンル</legend>
            <label v-for="genre in state.genres" :key="genre.id" class="check-row genre-chip">
              <input v-model="bookSearch.selectedGenreIds" :value="genre.id" type="checkbox" />
              <span>{{ genre.name }}</span>
            </label>
          </fieldset>
          <button type="submit">検索する</button>
        </form>
      </div>

      <div class="panel">
        <div class="panel-heading row-heading">
          <div>
            <p class="eyebrow">Results</p>
            <h2>検索結果</h2>
          </div>
          <p>{{ state.searchResults.length }} 件</p>
        </div>
        <div v-if="hasSearchResults" class="book-grid">
          <article v-for="book in state.searchResults" :key="book.googleVolumeId" class="book-card">
            <img v-if="book.thumbnailUrl" :src="book.thumbnailUrl" :alt="book.title" />
            <div v-else class="cover-fallback">画像なし</div>
            <div>
              <h3>{{ book.title }}</h3>
              <p>{{ joinAuthors(book) }}</p>
              <p class="muted">
                {{ book.publisher || '出版社未設定' }} /
                {{ book.publishedDate || '刊行日未設定' }}
              </p>
              <div class="actions">
                <button type="button" @click="saveBook(book)">保存して詳細へ</button>
              </div>
            </div>
          </article>
        </div>
        <div v-else-if="canShowEmptyState" class="empty-state">
          <h3>まだ候補がありません</h3>
          <p>キーワードを変えるか、Java、Python、Architecture などで検索してみてください。</p>
          <button type="button" class="secondary" @click="bookSearch.q = 'architecture'; searchBooks()">
            Architecture で探す
          </button>
        </div>
      </div>
    </section>

    <section v-show="activeView === 'detail'" v-if="state.selectedBook" class="grid detail-layout">
      <div class="panel">
        <div class="book-detail">
          <img
            v-if="state.selectedBook.thumbnailUrl"
            :src="state.selectedBook.thumbnailUrl"
            :alt="state.selectedBook.title"
          />
          <div v-else class="cover-fallback large">画像なし</div>
          <div>
            <p class="eyebrow">Selected Book</p>
            <h2>{{ state.selectedBook.title }}</h2>
            <p>{{ joinAuthors(state.selectedBook) }}</p>
            <p>{{ state.selectedBook.description || '説明はまだありません。' }}</p>
            <dl class="meta-list">
              <div>
                <dt>出版社</dt>
                <dd>{{ state.selectedBook.publisher || '未設定' }}</dd>
              </div>
              <div>
                <dt>出版日</dt>
                <dd>{{ state.selectedBook.publishedDate || '未設定' }}</dd>
              </div>
              <div>
                <dt>ISBN-10</dt>
                <dd>{{ state.selectedBook.isbn10 || '未設定' }}</dd>
              </div>
              <div>
                <dt>ISBN-13</dt>
                <dd>{{ state.selectedBook.isbn13 || '未設定' }}</dd>
              </div>
            </dl>
            <div v-if="state.selectedBook.tags?.length" class="tag-row">
              <button
                v-for="tag in state.selectedBook.tags"
                :key="tag.id"
                type="button"
                class="tag"
                @click="searchByTag(tag)"
              >
                {{ tag.name }}
              </button>
            </div>
            <p v-else class="muted">タグはまだありません。</p>
            <div class="actions">
              <button
                v-if="isLoggedIn && !hasFavorite(state.selectedBook)"
                type="button"
                @click="favoriteBook(state.selectedBook)"
              >
                お気に入りに追加
              </button>
              <button
                v-if="isLoggedIn && hasFavorite(state.selectedBook)"
                type="button"
                @click="unfavoriteBook(state.selectedBook)"
              >
                お気に入りから削除
              </button>
              <button v-if="!isLoggedIn" type="button" class="secondary" @click="showAuth('login')">
                ログインして保存
              </button>
            </div>
          </div>
        </div>
      </div>

      <div class="panel">
        <div class="panel-heading">
          <p class="eyebrow">Reviews</p>
          <h2>レビュー</h2>
        </div>
        <form v-if="isLoggedIn" class="form" @submit.prevent="submitReview">
          <label>
            星評価
            <select v-model="reviewForm.rating">
              <option v-for="rating in [5, 4, 3, 2, 1]" :key="rating" :value="rating">
                {{ rating }}
              </option>
            </select>
          </label>
          <label>
            本文
            <textarea v-model="reviewForm.body" rows="4" placeholder="読んだ理由や学び"></textarea>
          </label>
          <div class="actions">
            <button type="submit">レビューを保存</button>
            <button type="button" class="secondary" @click="deleteReview">自分のレビューを削除</button>
          </div>
        </form>
        <button v-else type="button" class="secondary" @click="showAuth('login')">
          ログインしてレビューする
        </button>
        <article v-for="review in state.reviews" :key="review.id" class="list-item">
          <h3>{{ review.user.displayName }} / 星 {{ review.rating }}</h3>
          <p>{{ review.body || '本文なし' }}</p>
          <p class="muted">{{ review.updatedAt || review.createdAt }}</p>
        </article>
        <div v-if="!hasReviews && canShowEmptyState" class="empty-state compact-empty">
          <h3>レビューはまだありません</h3>
          <p>読んだあとに、評価とメモを残せます。</p>
          <button v-if="!isLoggedIn" type="button" class="secondary" @click="showAuth('login')">
            ログインして最初のレビューを書く
          </button>
        </div>
      </div>
    </section>

    <section v-show="activeView === 'library'" class="grid two">
      <div class="panel">
        <div class="panel-heading">
          <p class="eyebrow">Favorites</p>
          <h2>お気に入り</h2>
        </div>
        <button v-if="isLoggedIn" type="button" class="secondary" @click="loadFavorites">更新</button>
        <button v-else type="button" class="secondary" @click="showAuth('login')">
          ログインして一覧を表示
        </button>
        <article v-for="book in state.favorites" :key="book.id" class="list-item compact">
          <h3>{{ book.title }}</h3>
          <p>{{ joinAuthors(book) }}</p>
          <button type="button" class="secondary" @click="selectBook(book)">詳細へ</button>
        </article>
        <div v-if="isLoggedIn && !hasFavorites && canShowEmptyState" class="empty-state compact-empty">
          <h3>お気に入りはまだありません</h3>
          <p>検索結果から保存した本を開き、お気に入りに追加できます。</p>
          <button type="button" class="secondary" @click="activeView = 'discover'">本を探す</button>
        </div>
      </div>

      <div class="panel">
        <div class="panel-heading">
          <p class="eyebrow">Ranking</p>
          <h2>ジャンルランキング</h2>
        </div>
        <form class="form inline-form" @submit.prevent="loadRanking">
          <label>
            ジャンル
            <select v-model="state.selectedGenreId">
              <option v-for="genre in state.genres" :key="genre.id" :value="genre.id">
                {{ genre.name }}
              </option>
            </select>
          </label>
          <button type="submit">表示</button>
        </form>
        <article v-for="row in state.ranking" :key="row.book.id" class="list-item compact">
          <h3>{{ row.book.title }}</h3>
          <p>
            お気に入り {{ row.favoriteCount }} / 平均 {{ formatRating(row.averageRating) }} /
            レビュー {{ row.reviewCount }}
          </p>
          <button type="button" class="secondary" @click="selectBook(row.book)">詳細へ</button>
        </article>
        <div v-if="!hasRanking && canShowEmptyState" class="empty-state compact-empty">
          <h3>ランキングはまだありません</h3>
          <p>書籍を保存し、お気に入りやレビューが増えるとここに並びます。</p>
          <button type="button" class="secondary" @click="activeView = 'discover'">検索へ戻る</button>
        </div>
      </div>
    </section>

    <section v-show="activeView === 'people'" class="panel">
      <div class="panel-heading row-heading">
        <div>
          <p class="eyebrow">People</p>
          <h2>ユーザー</h2>
        </div>
        <form class="lookup-form" @submit.prevent="loadUser()">
          <input v-model="userLookupId" type="number" min="1" placeholder="ユーザーID" />
          <button type="submit">表示</button>
        </form>
      </div>
      <div v-if="hasFeaturedUsers" class="user-grid">
        <article v-for="user in state.featuredUsers" :key="user.id" class="user-card">
          <img v-if="user.avatarUrl" :src="user.avatarUrl" :alt="user.displayName" />
          <div v-else class="avatar-fallback">{{ user.displayName.slice(0, 1) }}</div>
          <h3>{{ user.displayName }}</h3>
          <p>{{ user.headline || '見出し未設定' }}</p>
          <p class="muted">フォロワー {{ user.followerCount }}</p>
          <div class="actions">
            <button type="button" class="secondary" @click="loadUser(user.id)">プロフィール</button>
            <button v-if="isLoggedIn" type="button" @click="followUser(user)">フォロー</button>
            <button v-else type="button" class="secondary" @click="showAuth('login')">
              ログインしてフォロー
            </button>
          </div>
        </article>
      </div>
      <div v-else-if="canShowEmptyState" class="empty-state">
        <h3>ユーザーはまだ見つかりません</h3>
        <p>登録とフォローが増えると、ここにユーザーが表示されます。</p>
        <button v-if="!isLoggedIn" type="button" class="secondary" @click="showAuth('register')">
          アカウントを作る
        </button>
      </div>

      <article v-if="state.selectedUser" class="profile-panel">
        <h3>{{ state.selectedUser.displayName }}</h3>
        <p>{{ state.selectedUser.bio || 'プロフィール本文はまだありません。' }}</p>
        <p>{{ state.selectedUser.headline || '見出し未設定' }}</p>
        <p class="muted">フォロワー {{ state.selectedUser.followerCount }}</p>
        <div v-if="isLoggedIn" class="actions">
          <button type="button" @click="followUser(state.selectedUser)">フォロー</button>
          <button type="button" class="secondary" @click="unfollowUser(state.selectedUser)">
            フォロー解除
          </button>
        </div>
        <button v-else type="button" class="secondary" @click="showAuth('login')">
          ログインしてフォロー
        </button>
      </article>
    </section>
  </main>
</template>
